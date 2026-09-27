# todo-app

Minimal Spring Boot todo API used as the baseline for a Claude Code skills demo.

## Run

    ./mvnw test
    ./mvnw spring-boot:run

## API

| Method | Path                       | Description        |
|--------|----------------------------|--------------------|
| GET    | /api/todos                 | List todos         |
| POST   | /api/todos                 | Create a todo      |
| POST   | /api/todos/{id}/complete   | Mark todo complete |
