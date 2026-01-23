# Rangiffler

Приветствую!
Если ты это читаешь - то ты попал в репозиторий дипломного проекта QA.GURU Java Advanced.

Это проект Rangiffler - сервис для того, чтобы делиться с друзьями своими классными фотографиями из путешествий.

Приложение реализовано на микросервисной архитектуре. Отдельные модули передают друг другу данные с помощью gRPC, 
а общение с Gateway происходит по средствам GraphQL. Также для корректной работы с API и UI сервиса требуется
использование токена, так что будь готов зарегистрироваться и авторизоваться.

# Структура Rangiffler
Как было сказано ранее, Rangiffler построен на базе микросервисной архитектуры. Детальнее с схемой взаимодействия
сервисов друг с другом можно ознакомиться на изображении ниже.

<img src="services.png" width="600">

# Локальный запуск приложения
Для корректной локальной работы сервиса первоначально необходимо установить Docker Images. Сделать это можно с помощью следующих команд:
```posh
docker pull postgres:15.1
docker pull confluentinc/cp-zookeeper:7.3.2
docker pull confluentinc/cp-kafka:7.3.2
```

После `pull` вы увидите спуленный image командой `docker images`

```posh
~ % docker images            
REPOSITORY                 TAG              IMAGE ID       CREATED         SIZE
postgres                   15.1             9f3ec01f884d   10 days ago     379MB
confluentinc/cp-kafka      7.3.2            db97697f6e28   12 months ago   457MB
confluentinc/cp-zookeeper  7.3.2            6fe5551964f5   7 years ago     451MB
```

Далее следует выполнить скрипт, расположенный в корне проекта, в файле localenv.sh выполнив следующую команду в консоли:
```posh
rangiffler-diploma % bash localenv.sh
```
Также скрипт можно запустить напрямую в консоли, поочередно выполнив следующие команды:
```posh
docker run --name rangiffler-all -p 5432:5432 -e POSTGRES_PASSWORD=secret -v pgdata:/var/lib/postgresql/data -v ./postgres/script:/docker-entrypoint-initdb.d -e CREATE_DATABASES=rangiffler-auth,rangiffler-countries,rangiffler-userdata,rangiffler-photos -e TZ=GMT+3 -e PGTZ=GMT+3 -d postgres:15.1 -c max_prepared_transactions=100

docker run --name=zookeeper -e ZOOKEEPER_CLIENT_PORT=2181 -p 2181:2181 -d confluentinc/cp-zookeeper:7.3.2

docker run --name=kafka -e KAFKA_BROKER_ID=1 \
-e KAFKA_ZOOKEEPER_CONNECT=$(docker inspect zookeeper --format='{{ .NetworkSettings.IPAddress }}'):2181 \
-e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
-e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
-e KAFKA_TRANSACTION_STATE_LOG_MIN_ISR=1 \
-e KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR=1 \
-p 9092:9092 -d confluentinc/cp-kafka:7.3.2
```

Чтобы убедиться в корректном запуске всех сервисов в Docker, стоит выполнить следующую команду:
```posh
docker ps
```
Если в консоле отображаются все запущенные сервисы, как показано ниже, то можно переходить на следующий этап:
```posh
CONTAINER ID   IMAGE                             COMMAND                  CREATED          STATUS          PORTS                                        NAMES
b4759db53249   confluentinc/cp-kafka:7.3.2       "/etc/confluent/dock…"   19 minutes ago   Up 19 minutes   0.0.0.0:9092->9092/tcp                       kafka
5579fa1f58f5   confluentinc/cp-zookeeper:7.3.2   "/etc/confluent/dock…"   19 minutes ago   Up 19 minutes   2888/tcp, 0.0.0.0:2181->2181/tcp, 3888/tcp   zookeeper
e9fd38df2c3c   postgres:15.1                     "docker-entrypoint.s…"   19 minutes ago   Up 19 minutes   0.0.0.0:5432->5432/tcp                       rangiffler-all
```


#### 3. Как только у вас появилось уже 2 сервиса, есть смысл подумать о докеризации

  Чем раньше у ваc получится запустить в докере фронт и все бэкенды, тем проще будет дальше.
На самом деле, докеризация не является строго обязательным требованием, но если вы хотите в будущем
задеплоить свой сервис на прод, прикрутить CI/CD, без этого никак не обойдется.

  Я советую использовать плагин jib - как в niffler, для бэкендов, и самописный dockerfile для фронта.
Фронтенд использует React, докеризация там работает ровно так же, как и в Niffler.

##### Особенности реализации backend



###### Security config

   Для локального тестирования вы можете открыть доступ к antMatcher("/graphiql/**"), в конфиге `rangiffler-gateway`, но все запросы
на /graphql должны быть запрещены:
```java
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        corsCustomizer.corsCustomizer(http);

        http.authorizeHttpRequests(customizer ->
                customizer.requestMatchers(antMatcher("/graphiql/**"))
                        .permitAll()
                        .anyRequest()
                        .authenticated()
        ).oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }
```

#### 6. Подготовить структуру тестового "фреймворка", подумать о том какие прекондишены и как вы будете создавать

Здесь однозначно понадобится возможность API-логина и работы со всеми возможными preconditions проекта - фотками,
пользователями и т.д. Например, было бы хорошо иметь тесты примерно такого вида:
```java
@Test
@DisplayName("...")
@Tag("...")
@ApiLogin(user = @User(photos = @Photo(country = RUSSIA)))
void exampleTest(UserGql createdUser) { ... }

@Test
@DisplayName("...")
@Tag("...")
@ApiLogin(user = @TestUser(photos = @Photo(country = INDIA), partners = {
        @Partner(status = FRIEND, photos = @Photo(country = CANADA, imageClasspath = "cat.jpeg")),
        @Partner(status = INCOME_INVITATION, photos = @Photo(country = CANADA, imageClasspath = "dog.jpeg")),
        @Partner(status = OUTCOME_INVITATION, photos = @Photo(country = AUSTRALIA, imageClasspath = "fish.jpeg"))}))
void exampleTest2(UserGql createdUser) { ... }
```
