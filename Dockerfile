# 베이스 이미지 설정
FROM openjdk:17-jdk-slim

# 작업 디렉토리 생성
WORKDIR /app

# 외부에서 받아들일 변수 선언
ARG JAR_FILE

# JAR 파일 복사
COPY ${JAR_FILE} app.jar

# 필요한 포트 오픈
EXPOSE 8080
EXPOSE 8081

# 애플리케이션 실행
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
