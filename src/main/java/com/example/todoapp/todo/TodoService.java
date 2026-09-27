package com.example.todoapp.todo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class TodoService {

    private final TodoRepository repository;

    TodoService(TodoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    List<TodoResponse> list() {
        return repository.findAll(Sort.by("id")).stream()
                .map(TodoResponse::from)
                .toList();
    }

    @Transactional
    TodoResponse create(CreateTodoRequest request) {
        Todo saved = repository.save(new Todo(request.title()));
        return TodoResponse.from(saved);
    }

    @Transactional
    Optional<TodoResponse> complete(long id) {
        return repository.findById(id)
                .map(todo -> {
                    todo.complete();
                    return TodoResponse.from(todo);
                });
    }
}
