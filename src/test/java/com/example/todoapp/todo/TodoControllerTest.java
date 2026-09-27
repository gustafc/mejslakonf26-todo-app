package com.example.todoapp.todo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@WebMvcTest(TodoController.class)
class TodoControllerTest {

    @Autowired
    MockMvcTester mvc;

    @MockitoBean
    TodoService service;

    @Test
    void should_return_all_todos_when_listing() {
        // given
        given(service.list()).willReturn(List.of(
                new TodoResponse(1L, "Buy milk", false),
                new TodoResponse(2L, "Write slides", true)));

        // when
        var result = mvc.get().uri("/api/todos");

        // then
        assertThat(result).hasStatusOk()
                .bodyJson().isLenientlyEqualTo("""
                        [
                          { "id": 1, "title": "Buy milk", "done": false },
                          { "id": 2, "title": "Write slides", "done": true }
                        ]
                        """);
    }

    @Test
    void should_return_created_todo_when_title_is_valid() {
        // given
        given(service.create(any())).willReturn(new TodoResponse(1L, "Buy milk", false));

        // when
        var result = mvc.post().uri("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        { "title": "Buy milk" }
                        """);

        // then
        assertThat(result).hasStatus(HttpStatus.CREATED)
                .bodyJson().isLenientlyEqualTo("""
                        { "id": 1, "title": "Buy milk", "done": false }
                        """);
    }

    @Test
    void should_reject_request_when_title_is_blank() {
        // when
        var result = mvc.post().uri("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        { "title": "  " }
                        """);

        // then
        assertThat(result).hasStatus(HttpStatus.BAD_REQUEST);
        then(service).should(never()).create(any());
    }

    @Test
    void should_return_completed_todo_when_todo_exists() {
        // given
        given(service.complete(1L)).willReturn(Optional.of(new TodoResponse(1L, "Buy milk", true)));

        // when
        var result = mvc.post().uri("/api/todos/1/complete");

        // then
        assertThat(result).hasStatusOk()
                .bodyJson().isLenientlyEqualTo("""
                        { "id": 1, "title": "Buy milk", "done": true }
                        """);
    }

    @Test
    void should_return_not_found_when_completing_unknown_todo() {
        // given
        given(service.complete(99L)).willReturn(Optional.empty());

        // when
        var result = mvc.post().uri("/api/todos/99/complete");

        // then
        assertThat(result).hasStatus(HttpStatus.NOT_FOUND);
    }
}
