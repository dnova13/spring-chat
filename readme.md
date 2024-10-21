
## 소켓을 이용한 채팅

### 개발 환경
- java version 17
- spring boot version 3.4.x


### gradle spring boot 실행
```
./gradlew bootRun --continuous

```

### gradle spring build
```
./gradlew build

# 테스트 제외하고 빌드
./gradlew build -x test

```

### gradle dependencies sync.
```
./gradlew dependencies
```


### swagger 주소
```
{host}/swagger-ui
```

### docker 명령어 
```
docker build -t spring-boot-app .

docker run -p 8080:8080 -d chat-spring -n chat-network

docker compose up -d

docker-compose up  -d --build

docker-compose build --no-cache

```