# MeteoAPI

REST API для доступа к метеоданным: пользователи авторизуются по JWT, привязывают к себе станции и читают показания датчиков из удалённой БД с динамической схемой (таблица = номер станции, колонки = коды параметров).

## Стек

- **Spring Boot 4.0.5**, **Kotlin 2.3.20**, **JVM 25**
- **Spring MVC** + **Spring Security** + **JWT** (jjwt 0.12.6)
- **Spring Data JPA** (Hibernate) для локальной БД, **JdbcClient** для sensor-БД
- **MySQL 8**, **Flyway** для миграций
- **Docker** (multi-stage) + **docker-compose** для локальной разработки
- **GitHub Actions** + **ghcr.io** - CI и публикация образа
- **springdoc-openapi** - Swagger UI из коробки
- **Spotless** + **ktlint** - форматирование

## Архитектура

Монолит с двумя источниками данных:

- **Локальная БД** - источник истины для пользователей, станций, прав, справочника параметров. Доступ через JPA.
- **Удалённая sensor-БД** - read-only, показания датчиков. Доступ только через `JdbcClient` в `SensorRepository`; динамические имена таблиц и колонок валидируются.

```
src/main/kotlin/com/shestikpetr/meteoapi/
├── config/        # SensorDataSourceConfig, OpenAPI, Security
├── controller/    # AuthController, StationsController, DataController, HealthController
├── dto/           # common/, auth/, station/, sensor/
├── entity/        # BaseEntity, TimestampedEntity + JPA-сущности
├── exception/     # GlobalExceptionHandler + доменные исключения
├── repository/    # JPA-репозитории + SensorRepository (JdbcClient)
├── security/      # JwtService, JwtAuthenticationFilter, UserPrincipal
└── service/       # AuthService, UserStationService, StationParametersService, SensorDataService
migrations/        # Flyway, копируются в classpath:db/migration при сборке
```

## Эндпоинты

Публичные REST-маршруты живут под префиксом `/api/v1` (константы в `config/ApiRoutes.kt`).
Служебный `/health` намеренно без версии. Полная интерактивная документация - Swagger UI.

| Метод  | Путь                                                                  | Auth | Что делает                                          |
|--------|-----------------------------------------------------------------------|------|-----------------------------------------------------|
| POST   | `/api/v1/auth/register`                                               | -    | Регистрация: username/email/password → пара токенов |
| POST   | `/api/v1/auth/login`                                                  | -    | Логин: username+password → пара токенов             |
| POST   | `/api/v1/auth/refresh`                                                | -    | Обновить access по refresh-токену                   |
| GET    | `/api/v1/stations`                                                    | JWT  | Список станций, привязанных к пользователю          |
| POST   | `/api/v1/stations`                                                    | JWT  | Привязать станцию по `stationNumber`                |
| PATCH  | `/api/v1/stations/{stationNumber}`                                    | JWT  | Переименовать (`customName`)                        |
| DELETE | `/api/v1/stations/{stationNumber}`                                    | JWT  | Отвязать станцию                                    |
| GET    | `/api/v1/stations/{stationNumber}/parameters`                         | JWT  | Активные параметры станции                          |
| GET    | `/api/v1/stations/{stationNumber}/data`                               | JWT  | Последние значения всех параметров                  |
| GET    | `/api/v1/stations/{stationNumber}/parameters/{parameterCode}/history` | JWT  | Временной ряд значений параметра                    |
| GET    | `/health`                                                             | -    | Health-check приложения                             |

Все ответы обёрнуты в `ApiResponse<T> = { success, data?, error? }`. JSON в camelCase.

## Запуск

```bash
cp .env.example .env        # заполнить пароли, JWT_SECRET, sensor-БД
docker-compose up --build -d
```

- Swagger UI - http://localhost:8080/swagger-ui.html
- OpenAPI JSON - http://localhost:8080/v3/api-docs
- Health - http://localhost:8080/health

## CI/CD

Пайплайн - `.github/workflows/ci.yml`.

- на PR в `master` - `spotlessCheck`, тесты, `bootJar`;
- на push в `master` или тег `v*` - собирается и пушится Docker-образ в `ghcr.io/shestikpetr/meteoapi` с тегами `latest` / `sha-<short>` / `<semver>`;
- после публикации образа - автодеплой на прод-сервер по SSH (`docker compose pull && up -d`).

## Миграции

Лежат в `/migrations/` в корне репозитория; gradle-задача `processResources` копирует их в `classpath:db/migration`, Flyway накатывает при старте приложения. Руками в `flyway_schema_history` лезть не нужно - правим миграцию и релизим новую версию.
