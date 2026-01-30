FROM eclipse-temurin:21-jdk

COPY ./build/libs/*SNAPSHOT.jar project.jar

ENV TZ=Asia/Seoul

ENTRYPOINT ["java", "-jar", "/project.jar"]