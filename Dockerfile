# 베이스 이미지 설정
FROM openjdk:17-jdk-slim

# 작업 디렉토리 생성
WORKDIR /app

# 호스트의 JAR 파일을 컨테이너의 /app.jar 경로로 복사
COPY build/libs/demo-0.0.1-SNAPSHOT.jar /app.jar

EXPOSE 8080

# Spring Boot 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "/app.jar"]