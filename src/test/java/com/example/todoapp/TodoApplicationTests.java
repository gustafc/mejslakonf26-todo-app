package com.example.todoapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TodoApplicationTests {

    @Test
    void should_start_when_migrations_match_entities() {
        // ddl-auto=validate makes context startup fail if Flyway schema and JPA mappings diverge
    }
}
