# Chat Client Streaming

[Streaming Response in Spring AI ChatClient](https://www.baeldung.com/spring-ai-chatclient-stream-response)

#### Deployment

- Build the Docker image

```shell
docker build --build-arg JAR_FILE=target/your-app-name.jar -t demo-app:latest .
```

- Run the Docker container

```shell
docker run -p 8080:8080 -e "JAVA_OPTS=-Xmx512m -XX:MaxMetaspaceSize=128m" demo-app:latest --server.port=8080
```