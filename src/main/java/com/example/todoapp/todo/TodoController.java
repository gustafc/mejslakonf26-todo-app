package com.example.todoapp.todo;

import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/todos")
class TodoController {

    private final TodoService service;

    TodoController(TodoService service) {
        this.service = service;
    }

    @GetMapping
    List<TodoResponse> list() {
        return service.list();
    }

    @PostMapping
    ResponseEntity<TodoResponse> create(@Valid @RequestBody CreateTodoRequest request) {
        TodoResponse created = service.create(request);
        return ResponseEntity.created(URI.create("/api/todos/" + created.id())).body(created);
    }

    @PostMapping("/{id}/complete")
    ResponseEntity<TodoResponse> complete(@PathVariable long id) {
        return ResponseEntity.of(service.complete(id));
    }
}
