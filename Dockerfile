# Run Stage
FROM openjdk:17-jdk-slim

# curl 설치
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# 로그 디렉토리 생성
RUN mkdir -p logs

# 빌드 결과물 복사
COPY  ./build/libs/*.jar channeling.jar

# 포트 노출 및 실행
EXPOSE 8080

CMD ["java", "-jar", "channeling.jar"]