# Run Stage
FROM openjdk:17-jdk-slim

# curl 설치
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# 로그 디렉토리 생성 및 사용자 생성
RUN groupadd -g 1000 appuser && \
    useradd -u 1000 -g 1000 -m appuser && \
    mkdir -p logs && \
    chown -R 1000:1000 .

USER 1000

# 빌드 결과물 복사
COPY  ./build/libs/*.jar channeling.jar

# 포트 노출 및 실행
EXPOSE 8080

CMD ["java", "-jar", "channeling.jar"]