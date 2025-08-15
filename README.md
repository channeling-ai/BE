# channeling-backend

## 💡 Project Overview

유튜브 채널 및 개별 영상 데이터를 AI로 분석해, 개선점과 트렌드 기반 콘텐츠 아이디어를 제공하는 솔루션입니다.  
초보 유튜버부터 전문 크리에이터, 브랜드 마케팅 팀까지 모두가 활용할 수 있는 맞춤형 리포트를 자동 생성합니다.

<img width="810" alt="채널링 메인 페이지" src="https://github.com/user-attachments/assets/ad801b4f-d1ff-4e59-b72b-3246c8d20f28" />



## 프로젝트 소개
유튜브 채널 및 개별 영상 데이터를 AI로 분석해, 개선점과 트렌드 기반 콘텐츠 아이디어를 제공하는 솔루션입니다.  
초보 유튜버부터 전문 크리에이터, 브랜드 마케팅 팀까지 모두가 활용할 수 있는 맞춤형 리포트를 자동 생성합니다.



## 🎯 주요 기능

### 📊 **영상 URL 리포트 생성**
- 조회수, 시청 지속시간, 클릭률(CTR) 등 핵심 지표를 시각화  
- 썸네일·제목·태그·콘텐츠 길이 최적화 제안  
- 다음 콘텐츠 아이디어 및 연계 시리즈 추천

### 🔍 **채널 메타데이터 분석**
- 채널/영상 제목, 설명, 자막, 댓글, 좋아요 등의 정보를 수집·분석  
- 시청자 반응과 트렌드를 결합한 데이터 대시보드 제공

### 💡 **트렌드 키워드 분석**
- 구글 트렌드, 네이버 서치 트렌드, 급상승 영상 데이터를 교차 분석  
- 실시간 핵심 키워드와 콘텐츠 아이디어 추천

### 🔐 **구글 OAuth 인증**
- 유튜브 API·YouTube Analytics API를 활용해 채널 소유자 인증  
- 권한 기반 데이터 수집으로 보안 강화

## 팀원 소개

| ![텍스트](https://github.com/user-attachments/assets/1de9a92f-0662-4f6c-b0dd-7acf2167c934)| ![텍스트](https://github.com/user-attachments/assets/1de9a92f-0662-4f6c-b0dd-7acf2167c934) | ![텍스트](https://avatars.githubusercontent.com/u/82094699?v=4) | [![텍스트](https://github.com/user-attachments/assets/c32566d2-3204-49f3-a3cb-e3c6e17be95d)](https://github.com/Ochangmin524) |
|:---:|:---:|:---:|:---:|
| 준이/진준우 | 미노/송민혁 | 패트/허유진 | 티미/오창민 | 

## 기술 스택
<!--<img src="https://img.shields.io/badge/spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white"> <img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white"> <img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white"> <img src="https://img.shields.io/badge/langchain-1C3C3C?style=for-the-badge&logo=langchain&logoColor=white"> <img src="https://img.shields.io/badge/openai-412991?style=for-the-badge&logo=openai&logoColor=white"> <img src="https://img.shields.io/badge/FastAPI-009688?style=for-the-badge&logo=fastapi&logoColor=white"> <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white"> <img src="https://img.shields.io/badge/Kafka-231F20?style=for-the-badge&logo=apachekafka&logoColor=white">
-->
<img width="845" height="627" alt="image" src="https://github.com/user-attachments/assets/e3c4701f-eec1-4caa-9aa9-5ba6e59a2a49" />

## 서버 아키텍처 구조
<img width="1251" height="612" alt="image" src="https://github.com/user-attachments/assets/0ce377e2-8190-43fc-b480-271e28c0f4f9" />


## 📁 프로젝트 구조

```
BE/
├── src/main/java/channeling/be/
│   ├── domain/                # 도메인별 비즈니스 로직
│   │   ├── auth/              # 인증/인가
│   │   ├── channel/           # 채널 관리
│   │   ├── member/            # 회원 관리
│   │   ├── video/             # 영상 관리
│   │   ├── report/            # 리포트 생성
│   │   ├── comment/           # 댓글 분석
│   │   └── idea/              # 아이디어 추천
│   ├── global/                # 전역 설정
│   │   ├── config/            # Security, CORS 설정
│   │   └── infrastructure/    # 외부 API 연동
│   └── response/              # 공통 응답 처리
└── src/main/resources/
    └── application*.yml       # 환경 설정
```

## 🔧 컨벤션 전략

### 📝 Commit 타입

| 타입 | 설명 | 예시 |
|------|------|------|
| 🔨 **CHORE** | 프로덕션 코드가 바뀌지 않는 작업 | `[CHORE] 빌드 스크립트 추가` |
| 📦 **BUILD** | 빌드 관련 수정, 모듈 설치/삭제 | `[BUILD] 의존성 업데이트` |
| 📚 **DOCS** | 문서화 업데이트 | `[DOCS] README.md에 대한 설명 추가` |
| ✨ **FEAT** | 새로운 기능 추가 | `[FEAT] 통신 끊김 푸쉬 기능 추가` |
| 🐛 **FIX** | 버그 수정 | `[FIX] response 메시지 문제 제거` |
| 🚨 **HOTFIX** | 긴급 버그 수정 | `[HOTFIX] production에서 잘못 보내던 메시지 제거` |
| ♻️ **REFACTOR** | 코드 리팩토링 | `[REFACTOR] 4d3d3d3 커밋의 abuser check logic refactoring` |
| 💄 **STYLE** | 코드 스타일, 포맷 변경 | `[STYLE] 탭을 2칸 띄어쓰기로 바꿈` |
| 🧪 **TEST** | 테스트 코드 추가/수정 | `[TEST] PushService에 대한 Mock Test` |

### 📌 Commit 메시지 규칙

1. ✅ 대괄호 안에 해당 커밋의 타입을 적는다
2. ✅ 과거 시제 사용 X, 개조식 구문으로 작성
3. ✅ 무엇과 왜를 설명 (어떻게 X)
4. ✅ 마침표와 특수기호 사용 X

### 🌿 Git Branch 전략

#### 📋 브랜치 명명 규칙
1. ✅ 브랜치 이름은 영어로 작성
2. ✅ 슬래시(`/`)로 카테고리화, 대시(`-`)로 기능 연결

#### 📝 브랜치 예시
- 🎯 `feat/#0-init-structure`
- 🎯 `feat/disconnected-push`
- 🎯 `refactor/rename-variables`
- 🎯 `fix/wrong-type-declarations`

### 🔀 Branch Rules

| 브랜치 | 용도 | 설명 |
|--------|------|------|
| 🚀 **main** | 운영 배포 | 실제 서버 Release용 브랜치, MR 발생 시 자동 배포 |
| 🔧 **develop** | 개발 환경 | 개발 서버 배포용, 실제 배포 전 테스트 환경 |
| ✨ **feature/** | 기능 개발 | 새로운 기능 추가 |
| 🐛 **fix/** | 버그 수정 | 버그 수정 작업 |

### 🔄 Merge 전략

| 방향 | 전략 | 이유 |
|------|------|------|
| **main ← develop** | 📌 Merge Commit | 전체 커밋 이력 보존이 main에 적합 |
| **develop ← feature** | 📦 Squash Commit | 깔끔한 커밋 이력 유지 |

> 💡 **Tip**: conflict 발생 시 `git rebase` 사용
> 
> 🔙 **Rollback**: main 배포 복구 시 `revert` 사용




