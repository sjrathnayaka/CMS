# Card Management System

A Spring Boot application for managing credit cards using native SQL queries with JdbcTemplate (without JPA/Hibernate).

## Features

- Add new credit cards with validation
- Pure JDBC implementation using JdbcTemplate
- Native SQL queries (no JPA/Hibernate)
- PostgreSQL database
- Beautiful, responsive UI
- Automatic field handling (card status, available limits, timestamps)

## Technologies Used

- Spring Boot 4.0.2
- Spring JDBC (JdbcTemplate)
- PostgreSQL
- Thymeleaf
- Lombok
- Jakarta Validation
- Maven

## Database Setup

1. Make sure PostgreSQL is running on `localhost:5432`
2. Create a database named `cms_db`:
   ```sql
   CREATE DATABASE cms_db;
   ```
3. Update credentials in `src/main/resources/application.properties` if needed:
   ```properties
   spring.datasource.username=postgres
   spring.datasource.password=root
   ```

The application will automatically create the `cards` table on startup using `schema.sql`.

## Running the Application

1. Build the project:
   ```bash
   mvn clean install
   ```

2. Run the application:
   ```bash
   mvn spring-boot:run
   ```

3. Open your browser and navigate to:
   ```
   http://localhost:8080/cards/add
   ```

## Card Add Page Fields

### Manual Input Fields (Required):
- **Card Number**: 16-digit number (unique)
- **Expiry Date**: Future date
- **Credit Limit**: Amount greater than 0
- **Cash Limit**: Amount greater than 0

### Auto-Generated Fields (Not in POST request):
- **Card Status**: Automatically set to "INACTIVE"
- **Available Credit Limit**: Automatically set equal to Credit Limit
- **Available Cash Limit**: Automatically set equal to Cash Limit
- **Last Update Time**: Automatically recorded as timestamp

## Database Schema

The `cards` table structure:

```sql
CREATE TABLE cards (
    id BIGSERIAL PRIMARY KEY,
    card_number VARCHAR(16) NOT NULL UNIQUE,
    expiry_date DATE NOT NULL,
    credit_limit NUMERIC(15, 2) NOT NULL,
    cash_limit NUMERIC(15, 2) NOT NULL,
    card_status VARCHAR(20) NOT NULL DEFAULT 'INACTIVE',
    available_credit_limit NUMERIC(15, 2) NOT NULL,
    available_cash_limit NUMERIC(15, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

## Project Structure

```
src/main/java/edu/epic/cms/
├── controller/
│   └── CardController.java       # Handles HTTP requests
├── dao/
│   └── CardDao.java              # Data access with JdbcTemplate (native SQL)
├── mapper/
│   └── CardRowMapper.java        # Maps ResultSet to Card objects
├── model/
│   └── Card.java                 # POJO model class
├── service/
│   └── CardService.java          # Business logic
└── CmsApplication.java           # Main Spring Boot application

src/main/resources/
├── templates/cards/
│   ├── add.html                  # Add card form
│   └── success.html              # Success page
├── schema.sql                    # Database schema
└── application.properties        # Configuration
```

## Native SQL Queries

This application uses **pure native SQL queries** via `JdbcTemplate`:

- **INSERT**: Uses `PreparedStatement` with `RETURN_GENERATED_KEYS`
- **SELECT**: Uses `queryForObject()` and `query()` with `CardRowMapper`
- **UPDATE**: Uses parameterized SQL with `update()`
- **DELETE**: Uses parameterized SQL with `update()`

No JPA/Hibernate annotations or entity managers are used.

## Validation

Server-side validation using Jakarta Validation:
- `@NotBlank` for card number
- `@Pattern` for 16-digit validation
- `@NotNull` for required fields
- `@Future` for expiry date
- `@DecimalMin` for positive amounts

Client-side validation using HTML5 attributes.
