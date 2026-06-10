# Url ShortCut

REST-сервис для сокращения ссылок с JWT-аутентификацией.

## Технологии

- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
- PostgreSQL
- Liquibase
- JWT
- Maven
- Lombok

## Возможности

- Регистрация сайта
- Выдача уникального логина и пароля
- Авторизация через JWT
- Регистрация URL
- Генерация короткой ссылки
- Переадресация по короткому коду
- Подсчёт количества переходов
- Получение статистики по ссылкам

## REST API

### Регистрация сайта

```http
POST /registration
```

Тело запроса:

```json
{
  "site": "job4j.ru"
}
```

Ответ:

```json
{
  "registration": true,
  "login": "site_login",
  "password": "site_password"
}
```

### Авторизация

```http
POST /login
```

Тело запроса:

```json
{
  "login": "site_login",
  "password": "site_password"
}
```

Ответ:

```json
{
  "token": "jwt_token"
}
```

JWT передаётся в заголовке:

```http
Authorization: Bearer jwt_token
```

### Регистрация URL

```http
POST /convert
```

Тело запроса:

```json
{
  "url": "https://job4j.ru/profile/exercise/106/task-view/532"
}
```

Ответ:

```json
{
  "code": "ZRUfdD2"
}
```

### Переадресация

```http
GET /redirect/{code}
```

Ответ:

```http
HTTP/1.1 302 Found
Location: https://job4j.ru/profile/exercise/106/task-view/532
```

---

### Статистика

```http
GET /statistic
```

Ответ:

```json
[
  {
    "url": "https://job4j.ru/profile/exercise/106/task-view/532",
    "total": 103
  }
]
```

Такой подход предотвращает потерю данных при одновременных запросах нескольких пользователей.

## Запуск приложения

```bash
./mvnw spring-boot:run
```

## Сборка проекта

```bash
mvn clean package
```
