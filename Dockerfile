# Run Stage
FROM openjdk:17-jdk-slim

WORKDIR /app

# 빌드 결과물 복사
COPY  ./build/libs/*.jar channeling.jar

# 포트 노출 및 실행
EXPOSE 8080

CMD ["java", "-jar", "channeling.jar"]