# 1. OpenJDK 17 alpine 사용
FROM openjdk:17-jdk-alpine
#FROM openjdk:21-jdk-alpine

# 2. jar 파일을 /app 디렉토리에 복사
ARG PROJECT_NAME=chat-spring
ARG JAR_FILE=./build/libs/${PROJECT_NAME}-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

# 3. 애플리케이션을 실행
CMD ["java", "-jar", "/app.jar"]