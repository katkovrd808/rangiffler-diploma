FROM eclipse-temurin:21-jdk

WORKDIR /rangiffler-diploma
ENV TZ=Europe/Moscow
COPY ./gradle ./gradle
COPY ./rangiffler-e2e-tests ./rangiffler-e2e-tests
COPY ./rangiffler-grpc-common ./rangiffler-grpc-common
COPY ./gradlew ./
COPY ./build.gradle ./
COPY ./settings.gradle ./
COPY ./gradle.properties ./

CMD ./gradlew test -Dtest.env=docker -Drepository=jpa -Duser.timezone=Europe/Moscow
