package com.example.todoapp.todo;

import org.springframework.data.jpa.repository.JpaRepository;

interface TodoRepository extends JpaRepository<Todo, Long> {
}
