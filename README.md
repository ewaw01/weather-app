# 🌤️ Weather Application

**REST API** для работы с погодой и избранными локациями.  
Реализована **JWT-аутентификация**, ролевая модель (`USER` / `ADMIN`),  
**кэширование** погоды с автоматическим обновлением и **unit-тесты** с покрытием ~75%.

---

## 📚 Оглавление

- [О проекте](#-о-проекте)
- [Стек технологий](#-стек-технологий)
- [Что умеет приложение](#-что-умеет-приложение)
- [Проблемы и решения](#-проблемы-и-решения)
- [Как запустить](#-как-запустить)
- [API-эндпоинты](#-api-эндпоинты)
- [Примеры запросов](#-примеры-запросов-postman)
- [Тестирование](#-тестирование)
- [Дальнейшие планы](#-дальнейшие-планы)
- [Контакты](#-контакты)

---

## О проекте

Проект создан для отработки навыков разработки **REST API** на **Spring Boot** с акцентом на:

- реальную **JWT-аутентификацию** и **ролевую авторизацию**
- **Many-to-Many** связь между пользователями и локациями
- **кэширование** данных погоды с инвалидацией
- **глобальную обработку ошибок** и **логирование**
- полноценное **тестирование** (юнит + интеграционное)

---

## Стек технологий

| Категория | Технологии |
|-----------|-------------|
| **Язык** | Java 21 |
| **Фреймворк** | Spring Boot 3, Spring Security, Spring Data JPA |
| **Безопасность** | JWT (JJWT), BCrypt |
| **Базы данных** | PostgreSQL, H2 (тесты) |
| **Тестирование** | JUnit 5, Mockito, Spring Boot Test |
| **Сборка** | Maven |
| **Инструменты** | Git, Postman, IntelliJ IDEA, Docker (база) |

---

## Что умеет приложение

### 👤 Пользователи
- Регистрация и логин (JWT)
- Просмотр всех пользователей (только ADMIN)
- Обновление своих данных (USER) или любых (ADMIN)
- Удаление пользователя (только ADMIN)

### 📍 Локации
- Добавление локации в избранное (USER — себе, ADMIN — любому)
- Просмотр избранных локаций пользователя
- Удаление локации из избранного
- Защита от дублей (нельзя добавить одну локацию дважды)

### 🌦️ Погода
- Получение погоды по названию города
- **Умное кэширование**: данные хранятся 3 часа, затем обновляются

### 🧩 Дополнительно
- **Пагинация** и **динамические фильтры**
- **Глобальная обработка ошибок**
- **Логирование** всех ключевых событий
- **Unit-тесты** (покрытие ~75%)

---

## Проблемы и решения

**_Проблема_**: Поначалу я хранил локации пользователя отдельным полем в БД в виде строки с пробелами ("москва париж зимбабве ").
То есть у меня эта строка сплитилась просто по пробелу, когда нужна была локация, допустим для проверки на наличие.
Потом я понял, что это очень нестабильно, так как могут быть локации, которые содержат названия других локаций в своем названии.
Плюс к этому, для того, чтобы получить локацию, я каждый раз обращался к БД.
Решением стало реализация связи Many-to-Many с помощью вспомогательной таблицы со связями users_locations.
Понял, что никогда не стоит хранить связные данные в строке, это можно назвать анти-паттерном.

**_Проблема_**: Данные о погоде не обновлялись в случае, если временной интервал тот же, но день другой.
Просто добавил новое поле lastUpdated к энтити локации, плюс написал проверку.

**_Проблема_**: TransientPropertyValueException ошибка при сохранении пользователя с новой локацией. Из-за того, что
сохранял пользователя раньше локации, возникала ошибка трансиентности, так как локация оставалась в, так сказать, незавершенном состоянии.

**_Проблема_**: В UserEntity не работали аннотации @CreatedDate и @LastModifiedDate. Добавил аннотацию @EnableJpaAuditing в главном классе WeatherApplication
и добавил @EntityListeners(AuditingEntityListener.class) в UserEntity.

**_Проблема_**: После добавления security и jwt-аутентификации нужно было переписать тесты контроллера, старая аннотация @WebMvcTest уже не подходила, так как
она загружала только контекст контроллеров, а у меня, во-первых, стоит аннотация @EnableJpaAuditing над главным классом, которая создает JPA-модель, во-вторых,
теперь, прежде чем запрос перейдет к контроллерам, он сначала идет через фильтры security и проверки jwt, из-за чего тоже возникают проблемы. Было принято решение
использовать аннотацию @SpringBootTest, которая загружает весь контекст. Теперь мы можем заинжектить репозиторий и все нужные бины для security.

**_Проблема_**: Есть такие запросы, как обновление данных пользователя. У меня работало так, что любой пользователь мог поменять данные другому. Было принято передавать в подобные
методы контроллера текущего авторизованного пользователя UserDetails, и уже в сервисе сверять его с передаваемыми данными.

## Как запустить

### 1. Требования
- Java 21 (`java -version`)
- Maven 3.8+ (`mvn -version`)
- PostgreSQL (`psql --version`)

### 2. Клонирование
```bash
git clone https://github.com/ewaw01/weather-app.git
cd weather-app
```

### 3. Создать БД
Через Docker (рекомендуется):

```bash
docker run --name weather-postgres \
  -p 5432:5432 \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=your_password \
  -e POSTGRES_DB=weather_db \
  -d postgres
```

Через локальный PostgreSQL:

```sql
CREATE DATABASE weather_db;
```

### 4. Получить API-ключ OpenWeatherMap
Зарегистрироваться на OpenWeatherMap

Скопировать API-ключ в разделе «My API Keys»

### 5. Настроить application.properties
Создай файл src/main/resources/application.properties:

```properties
# База данных
spring.datasource.url=jdbc:postgresql://localhost:5432/weather_db
spring.datasource.username=postgres
spring.datasource.password=your_password

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# OpenWeatherMap
openweather.api.key=YOUR_API_KEY_HERE
openweather.api.base-url=https://api.openweathermap.org/data/2.5
openweather.defaults.units=metric
openweather.defaults.lang=ru

# JWT
jwt.secret=YOUR_SECRET_KEY_HERE
jwt.expiration=86400000
```

### 6. Запуск

```bash
./mvnw spring-boot:run
```

## API-эндпоинты
Базовый URL: http://localhost:8080

### Аутентификация (/api/auth)
| Метод | Эндпоинт | Описание |
|-------|----------|----------|
| POST | `/api/auth/register` | Регистрация нового пользователя |
| POST | `/api/auth/login` | Логин, возвращает JWT-токен |

### Основные эндпоинты (/api/weather)
| Метод | Эндпоинт | Доступ | Описание |
|-------|----------|--------|----------|
| POST | `/admin/users` | `ADMIN` | Создать пользователя |
| GET | `/admin/users` | `ADMIN` | Получить всех пользователей (пагинация) |
| DELETE | `/admin/users/{id}` | `ADMIN` | Удалить пользователя |
| DELETE | `/admin/locations/{id}` | `ADMIN` | Удалить локацию из кэша |
| PUT | `/users/{userId}/locations` | `USER` / `ADMIN` | Добавить локацию в избранное |
| PUT | `/users/{id}` | `USER` / `ADMIN` | Обновить данные пользователя |
| GET | `/users/{id}/locations` | `USER` / `ADMIN` | Получить избранные локации |
| DELETE | `/users/{id}/locations` | `USER` / `ADMIN` | Удалить локацию из избранного |
| GET | `/info/locations` | `USER` | Получить погоду по названию |
| GET | `/locations` | `USER` | Получить все локации (пагинация) |

## Примеры запросов (Postman)

1. Регистрация
```http
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "email": "user@test.com",
  "password": "123456",
  "username": "john"
}
```

2. Логин (получить JWT)
```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "user@test.com",
  "password": "123456"
}
```

3. Добавить локацию (USER)
```http
PUT http://localhost:8080/api/weather/users/1/locations
Authorization: Bearer <JWT>
Content-Type: application/json

{
  "name": "Москва"
}
```

4. Получить погоду
```http
GET http://localhost:8080/api/weather/info/locations?name=москва
Authorization: Bearer <JWT>
```

5. Получить избранные локации
```http
GET http://localhost:8080/api/weather/users/1/locations
Authorization: Bearer <JWT>
```

6. Удалить локацию
```http
DELETE http://localhost:8080/api/weather/users/1/locations?name=москва
Authorization: Bearer <JWT>
```

## Тестирование
Проект покрыт unit-тестами и интеграционными тестами:

MainServiceTest — бизнес-логика (Mockito)

WeatherControllerTest — REST API (MockMvc, JWT)

UserRepositoryTest — репозитории (DataJpaTest)

Всего тестов: 40+
Покрытие кода: ~75%

### Запуск тестов:

```bash
./mvnw test
```

## Дальнейшие планы
1. Добавить Redis для эффективного кэширования

2. Реализовать refresh-токены

3. Контейнеризация через Docker Compose

4. Подключить Swagger/OpenAPI

5. Настроить CI/CD (GitHub Actions)

6. Сделать Telegram-бота для получения погоды

7. Функционал — сохранять историю запросов пользователя, автоматическое определение города по IP

## 📫 Контакты
GitHub: github.com/ewaw01

Telegram: @voin_drakona2000
