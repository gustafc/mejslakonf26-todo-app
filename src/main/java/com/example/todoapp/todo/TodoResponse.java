package com.example.todoapp.todo;

public record TodoResponse(
        Long id,
        String title,
        boolean done
) {

    static TodoResponse from(Todo todo) {
        return new TodoResponse(todo.getId(), todo.getTitle(), todo.isDone());
    }
}
