# CRM System for Sellers and Transactions

## Описание проекта

Этот проект представляет собой упрощённую CRM-систему, разработанную на Java с использованием Spring Boot и PostgreSQL. Основная цель системы — управление данными о продавцах и их транзакциях, а также предоставление аналитических функций для оценки производительности продавцов.

## Функциональность

- **Управление продавцами:**
  - Создание, обновление и удаление продавцов.
  - Получение списка всех продавцов и информации о конкретном продавце.

- **Управление транзакциями:**
  - Создание, обновление и удаление транзакций.
  - Получение списка всех транзакций и информации о конкретной транзакции.

- **Аналитика:**
  - Вывод самого продуктивного продавца за день, месяц, квартал и год.
  - Вывод списка продавцов с суммой транзакций за выбранный период меньше заданной суммы.
  - Определение наилучшего периода времени для конкретного продавца с максимальным количеством транзакций.

## Инструкции по сборке и запуску

### Требования

- Java 21
- Gradle
- PostgreSQL

### Установка и настройка

1. Клонируйте репозиторий на локальный компьютер:
   ```bash
   git clone github.com/DmitryMustk/ShiftLabTask
   cd demo
   ```

2. Установите зависимости с помощью Gradle:
   ```bash
   ./gradlew build
   ```

3. Настройте подключение к базе данных в файле `application.yml`.

4. Запустите приложение:
   ```bash
   ./gradlew bootRun
   ```

## Примеры использования API

### Получение всех продавцов

**Запрос:**
```http
GET /api/sellers
```

**Ответ:**
```json
[
    {
        "id": 1,
        "name": "Seller One",
        "contact_info": "contact1@example.com",
        "registration_date": "2024-01-01T00:00:00"
    },
    {
        "id": 2,
        "name": "Seller Two",
        "contact_info": "contact2@example.com",
        "registration_date": "2024-01-05T00:00:00"
    }
]
```

### Создание нового продавца

**Запрос:**
```http
POST /api/sellers?seller_name=Seller Three&seller_contact_info=contact3@example.com
```

**Ответ:**
```json
{
    "id": 3,
    "name": "Seller Three",
    "contact_info": "contact3@example.com",
    "registration_date": "2024-01-10T00:00:00"
}
```

### Получение самого продуктивного продавца за день

**Запрос:**
```http
GET /api/sellers/productive/daily?date=2024-01-10
```

**Ответ:**
```json
{
    "id": 2,
    "name": "Seller Two",
    "total_amount": 500.00
}
```

## Зависимости

- Spring Boot
- Spring Web
- Spring Data JPA
- PostgreSQL Driver
- Lombok
- JUnit (для тестирования)
- Mockito (для тестирования)
