# Build stage
FROM ghcr.io/graalvm/graalvm-community:21 as builder
# 파일이름 지정
ENV JAR_FILE=drawingm.jar

# 작업 디렉토리 지정
WORKDIR /app

# 그래들 래퍼 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Gradle 래퍼 실행 권한 부여
RUN chmod +x ./gradlew

# 소스 코드 복사
COPY src src

# 애플리케이션 빌드 및 테스트 실행
RUN ./gradlew build --no-daemon --exclude-task test

# Runtime stage
FROM ghcr.io/graalvm/graalvm-community:21

# 첫 번째 스테이지에서 빌드된 JAR 파일 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# Spring Boot 애플리케이션 실행
ENTRYPOINT ["java", "-Dspring.profiles.active=product", "-jar", "app.jar"]