# 베이스 이미지 설정
FROM openjdk:17-jdk-slim

# 작업 디렉토리 생성
WORKDIR /app

COPY build/libs/demo-0.0.1-SNAPSHOT.jar /app/app.jar
# 필요한 포트 오픈
EXPOSE 9999

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
