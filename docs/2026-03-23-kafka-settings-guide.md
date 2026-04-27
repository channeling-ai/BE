# Channeling Kafka BE->LLM [A파트 정리]

> 작성일: 2026-03-23
> 대상: BE / LLM 팀 전체
> 관련 브랜치: refactor/#300-kafka-producer-setting

---

## 메시지 흐름

```
[Spring BE] ──── overview-topic-v2 ────→ [FastAPI LLM]
            ──── analysis-topic-v2 ───→
```

- 두 토픽은 하나의 Kafka 트랜잭션으로 묶여 원자적으로 발행됨
- 둘 다 성공하거나 둘 다 abort됨

---

## 프로듀서 (Spring BE)

### 1. acks

| 설정 | 값 | 의미 |
|------|----|------|
| `acks` | `all` | 리더 + 모든 ISR 복제본이 저장을 확인해야 ack 반환 |

> 트랜잭셔널 프로듀서는 `acks=all`이 강제 적용된다. 다른 값으로 변경 불가.

### 2. 파티션 전략

| 항목 | 현재 설정 |
|------|----------|
| 메시지 key | **미지정** (null) |
| 파티셔너 | DefaultPartitioner → key가 null이면 **RoundRobin** |
| 파티션 수 | 1 (환경변수 `KAFKA_TOPIC_PARTITIONS`로 변경 가능) |

**현재 파티션 1개이므로 순서 보장됨.** 파티션을 늘릴 경우:
- key가 null이면 RoundRobin으로 분산 → **메시지 순서 보장 안 됨**
- 순서가 필요하면 `report_id`를 key로 지정하여 같은 리포트의 메시지를 같은 파티션으로 라우팅해야 함

### 3. 재시도

| 설정 | 값 | 설명 |
|------|----|------|
| `retries` | `Integer.MAX_VALUE` | 트랜잭셔널 프로듀서 기본값 (무한 재시도) |
| `retry.backoff.ms` | `1000` | 재시도 간격 1초 |
| `delivery.timeout.ms` | `10000` | send()~최종 ack까지 최대 10초 |
| `request.timeout.ms` | `5000` | 단일 전송 요청 타임아웃 |

**재시도 동작 방식:**
```
send 시도 → 실패 → 1초 대기 → 재시도 → 실패 → 1초 대기 → ...
└── 이 전체 과정이 delivery.timeout.ms(10초) 이내에 끝나야 함
└── 10초 초과 시 TimeoutException → 트랜잭션 abort
```

실질적으로 `(10000 - 5000) / 1000 ≈ 4~5회` 재시도 가능.

### 4. 멱등성 (Idempotent Producer)

| 설정 | 값 | 설명 |
|------|----|------|
| `enable.idempotence` | `true` | 트랜잭셔널 프로듀서 기본값 (강제) |
| `max.in.flight.requests.per.connection` | `5` | 멱등성 보장 범위 내 최대 동시 요청 |

**멱등성이란:** 네트워크 오류로 재시도 시 브로커가 중복 메시지를 자동 감지·폐기한다.
- 프로듀서가 각 메시지에 `PID(Producer ID) + Sequence Number`를 부여
- 브로커가 이미 저장된 시퀀스와 비교하여 중복 차단
- 재시도로 인한 순서 역전도 방지 (시퀀스 기반 정렬)

### 5. 트랜잭션

| 항목 | 값 |
|------|----|
| `transactional.id` prefix | `channeling-be-tx-` |
| 실제 ID | `channeling-be-tx-{인스턴스별 고유 suffix}` (Spring Kafka가 자동 부여) |

**트랜잭션 범위:**
```
executeInTransaction() {
    send(overview-topic-v2, message)   ← 트랜잭션에 포함
    send(analysis-topic-v2, message)   ← 트랜잭션에 포함
}
// → 두 send 모두 성공 시 commit, 하나라도 실패 시 abort
```

**트랜잭션이 보장하는 것:**
- 두 토픽의 메시지가 **모두 보이거나 모두 안 보임** (컨슈머가 `read_committed`일 때)
- abort된 메시지는 `read_committed` 컨슈머에게 노출되지 않음

**트랜잭션이 보장하지 않는 것:**
- DB 트랜잭션과의 연동 (DB 커밋 후 Kafka 발행이 실패할 수 있음)
- 현재는 DB 커밋 후(`AFTER_COMMIT`) Kafka 발행하므로, Kafka 실패 시 Redis fallback으로 처리

---

## 컨슈머 (FastAPI LLM)

### 1. group.id

| 설정 | 값 |
|------|----|
| `group.id` | `llm-service-group` |

같은 group.id의 컨슈머끼리 파티션을 분배받아 메시지를 나눠 처리한다.
현재 파티션 1개이므로 같은 그룹 내 컨슈머 1개만 활성 소비한다.

### 2. isolation.level (트랜잭셔널 프로듀서 대응)

| 설정 | 값 | 필수 여부 |
|------|----|----------|
| `isolation_level` | `read_committed` | **필수** |

```python
@self.broker.subscriber(
    topic,
    isolation_level="read_committed",
    ...
)
```

| isolation.level | 동작 | 위험 |
|----------------|------|------|
| `read_uncommitted` (기본값) | abort된 트랜잭션 메시지도 소비 | **처리하면 안 되는 메시지를 처리할 수 있음** |
| `read_committed` | commit된 트랜잭션 메시지만 소비 | 없음 (정상) |

> **BE가 트랜잭셔널 프로듀서를 사용하므로 LLM 컨슈머는 반드시 `read_committed`를 설정해야 한다.**

### 3. 신뢰성 설정 (프로듀서 acks와의 쌍)

프로듀서 `acks=all`과 컨슈머 `isolation_level=read_committed`가 쌍으로 동작한다:

```
프로듀서 acks=all  →  모든 ISR에 저장된 후에만 ack
                      ↓
브로커              →  트랜잭션 commit 마커 기록
                      ↓
컨슈머 read_committed → commit 마커가 있는 메시지만 소비
```

이 조합이 **메시지 유실 없이 정확한 전달**을 보장한다.

### 4. 오프셋 관리

| 설정 | 값 | 설명 |
|------|----|------|
| `auto_commit` | `True` | 5초 간격 자동 커밋 |
| `auto_commit_interval_ms` | `5000` | 커밋 주기 |
| `auto_offset_reset` | `earliest` | 오프셋 없을 때 처음부터 읽기 |

**auto_offset_reset 선택지:**

| 값 | 동작 | 적합한 경우 |
|----|------|------------|
| `earliest` | 토픽의 가장 오래된 메시지부터 | 메시지 유실 방지 우선 (현재 설정) |
| `latest` | 구독 시점 이후 메시지만 | 실시간 처리만 필요할 때 |

**auto_commit=True의 트레이드오프:**
- 처리 중 크래시 시 → 오프셋이 이미 커밋되었으면 메시지 유실 가능
- 현재는 의도적 선택: LLM 처리 실패 시 Redis pub/sub으로 별도 알림하므로 재처리보다 빠른 실패 알림 우선

### 5. 성능 튜닝

**프로듀서 측 (BE):**

| 설정 | 값 | 역할 |
|------|----|------|
| `compression.type` | `snappy` | 배치 단위 압축 (LLM에 `python-snappy` 필요) |
| `batch.size` | `16384` (16KB) | 파티션당 배치 크기 |
| `linger.ms` | `5` | 배치 채우기 위한 최대 대기 시간 |
| `buffer.memory` | `33554432` (32MB) | 미전송 메시지 버퍼 |

**컨슈머 측 (LLM):**

| 설정 | 현재 값 | 비고 |
|------|---------|------|
| `auto_commit_interval_ms` | `5000` | FastStream 기본값 사용 |

> 현재 메시지 볼륨이 낮아 (리포트 생성 요청 단위) 별도 컨슈머 성능 튜닝 불필요.
> 볼륨 증가 시 `max.poll.records`, `fetch.min.bytes` 등 조정 검토.

### 6. 파티션 전략

| 항목 | 현재 상태 |
|------|----------|
| 파티션 수 | 1개 (토픽당) |
| 컨슈머 수 | 1개 (llm-service-group 내) |
| 파티션 할당 | 자동 (1:1 매핑) |

**스케일 아웃 시나리오:**

| 파티션 수 | 컨슈머 수 | 동작 |
|----------|----------|------|
| 1 | 1 | 현재 상태. 순서 보장됨 |
| 1 | 2+ | 1개만 활성 소비, 나머지 대기 (의미 없음) |
| N | N | 파티션별 1 컨슈머. 병렬 처리 가능 |
| N | N 초과 | 초과분 유휴 상태 |

> 파티션 확장 시 프로듀서에 메시지 key(`report_id`) 지정 필요. 미지정 시 RoundRobin으로 같은 리포트의 overview/analysis가 다른 파티션으로 분산되어 처리 순서를 보장할 수 없다.

---

## 설정값 요약

### 프로듀서 (Spring BE - KafkaProducerConfig)

```
bootstrap.servers          = ${KAFKA_BOOTSTRAP_SERVERS:localhost:9092}
key.serializer             = StringSerializer
value.serializer           = JsonSerializer
acks                       = all (트랜잭셔널 강제)
enable.idempotence         = true (트랜잭셔널 강제)
retries                    = MAX_VALUE (트랜잭셔널 강제)
transactional.id           = channeling-be-tx-{suffix}
retry.backoff.ms           = 1000
delivery.timeout.ms        = 10000
request.timeout.ms         = 5000
compression.type           = snappy
batch.size                 = 16384
linger.ms                  = 5
buffer.memory              = 33554432
```

### 컨슈머 (FastAPI LLM - kafka_config.py + base_consumer.py)

```
bootstrap.servers          = ${KAFKA_BOOTSTRAP_SERVERS:localhost:9092}
group.id                   = llm-service-group
isolation.level            = read_committed
auto.offset.reset          = earliest
enable.auto.commit         = true
auto.commit.interval.ms    = 5000
```

### 토픽 (KafkaTopicConfig)

```
overview-topic-v2          partitions=1  replicas=1
analysis-topic-v2          partitions=1  replicas=1
```
