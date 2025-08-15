# channeling-backend

## 💡 Project Overview

유튜브 채널 및 개별 영상 데이터를 AI로 분석해, 개선점과 트렌드 기반 콘텐츠 아이디어를 제공하는 솔루션입니다.  
초보 유튜버부터 전문 크리에이터, 브랜드 마케팅 팀까지 모두가 활용할 수 있는 맞춤형 리포트를 자동 생성합니다.

<img width="810" alt="채널링 메인 페이지" src="https://github.com/user-attachments/assets/ad801b4f-d1ff-4e59-b72b-3246c8d20f28" />



## 프로젝트 소개
유튜브 채널 및 개별 영상 데이터를 AI로 분석해, 개선점과 트렌드 기반 콘텐츠 아이디어를 제공하는 솔루션입니다.  
초보 유튜버부터 전문 크리에이터, 브랜드 마케팅 팀까지 모두가 활용할 수 있는 맞춤형 리포트를 자동 생성합니다.



## 주요 기능

- **영상 URL 리포트 생성**  
  - 조회수, 시청 지속시간, 클릭률(CTR) 등 핵심 지표를 시각화  
  - 썸네일·제목·태그·콘텐츠 길이 최적화 제안  
  - 다음 콘텐츠 아이디어 및 연계 시리즈 추천

- **채널 메타데이터 분석**  
  - 채널/영상 제목, 설명, 자막, 댓글, 좋아요 등의 정보를 수집·분석  
  - 시청자 반응과 트렌드를 결합한 데이터 대시보드 제공

- **트렌드 키워드 분석**  
  - 구글 트렌드, 네이버 서치 트렌드, 급상승 영상 데이터를 교차 분석  
  - 실시간 핵심 키워드와 콘텐츠 아이디어 추천

- **구글 OAuth 인증**  
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


## 브렌치 전략
### Commit 타입

---

- CHORE: 프로덕션 코드가 바뀌지 않고 개발 로직과 상관 없는 가벼운 일들
- BUILD : 빌드 관련 수정, 모듈 설치 또는 삭제 등
- DOCS: 도큐먼트/문서화 업데이트
- **FEATURE(FEAT): 새로운 기능/특징**
- **FIX: 버그를 고침**
- HOTFIX: 시급한 버그를 고침 - 현 production에 critical
- REFACTOR: production 코드를 리팩토링
- STYLE: Code의 스타일, 포맷 등이 바뀐 경우 - 세미콜론(;)이 빠졌다거나 등
- TEST: 테스트 코드 추가 및 업데이트

### Commit 메시지

---

1. 대괄호 안에 해당 커밋의 타입을 적는다.
2. 커밋의 내용은 과거 시제를 사용하지 않고, 개조식 구문(간결하고 요점적인 서술)로 적는다.
3. 본문은 어떻게 보다는 무엇과 왜를 설명한다.
4. 마침표와 특수기호는 사용하지 않는다

### commit type: 현재 형으로 무엇을 했는지 적되, 개조식 구문으로 적기

**Examples**

- `[CHORE] 빌드 스크립트 추가`
- `[DOCS] README.md에 대한 설명 추가`
- `[FEAT] 통신 끊김 푸쉬 기능 추가`
- `[FIX] response 메시지 문제 제거`
- `[HOTFIX] production에서 잘못 보내던 메시지 제거`
- `[REFACTOR] 4d3d3d3 커밋의 abuser check logic refactoring`
- `[STYLE] 탭을 2칸 띄어쓰기로 바꿈`
- `[TEST] PushService에 대한 Mock Test`

### Git branch 이름 작명

---

1. 브랜치 이름은 영어로 짓는다.
2. 슬래시(`/`)로 카테고리화 시키고, 뒤에 붙는 기능 및 내용을 대표하는 문구는 대시(`-`)로 연결한다.

### branch type: 대표 내용을 간단한 단어의 조합으로 표기

**Examples**

- `feat/#0-init-structure`
- `feat/disconnected-push`
- `refactor/rename-variables`
- `fix/wrong-type-declarations`



### Branch Rules

---

- **main** : 이 브랜치는 실제로 서버 Release를 위해 사용되고 있는 브랜치. 실제 배포는 이 브랜치에 MR이 발생할 때 일어남.
- **develop** : 이 브랜치는 서버를 미리 배포해볼 수 있는 브랜치. 실제 배포 전에 이 브랜치에서 확인할 수 있고, 해당 브랜치에 개발한 내역들이 쌓임. 실제 배포하기 전까지 main 브랜치 역할을 한다.
- **그 외** : 기능 추가(feature) 및 수정(fix)



### Merge 관련

---

- **main ← develop** : Merge commit을 남기는 방법으로 작업하기. 전체적인 커밋 이력이 남는게 main에 더 적합하다고 생각되기 때문!
* main에 배포된 것을 복구하고 싶을 때 → revert를 통해 재배포
- **develop ← feature** :  Squash Commit을 남기는 방법으로 작업하기. develop에는 구체적인 모든 사항을 깔끔하게 정리해서 올리는 것이 맞다고 생각되기 때문!
- conflict가 발생했다면, **git rebase**를 사용하기



### 서버 아키텍처 구조


---
<img width="1251" height="612" alt="image" src="https://github.com/user-attachments/assets/0ce377e2-8190-43fc-b480-271e28c0f4f9" />
