package com.kiosite.kiositetest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kiosite.kiositetest.config.SecurityConfig;
import com.kiosite.kiositetest.entity.Status;
import com.kiosite.kiositetest.entity.Task;
import com.kiosite.kiositetest.exception.InvalidStatusException;
import com.kiosite.kiositetest.exception.NotFoundException;
import com.kiosite.kiositetest.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
@Import(SecurityConfig.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    @Test
    @WithMockUser
    void testGetTaskByIdSuccess() throws Exception {
        UUID id = UUID.randomUUID();
        Task task = Task.builder().title("Test Task").status(Status.PENDING).build();
        when(taskService.getTaskById(id)).thenReturn(task);

        mockMvc.perform(get("/tasks/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @Test
    @WithMockUser
    void testGetTaskByIdNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(taskService.getTaskById(id)).thenThrow(new NotFoundException("Task not found"));

        mockMvc.perform(get("/tasks/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Task not found"));
    }

    @Test
    @WithMockUser
    void testCreateTaskSuccess() throws Exception {
        Task task = Task.builder().title("New Task").status(Status.PENDING).build();
        when(taskService.createTask(any())).thenReturn(task);

        String json = objectMapper.writeValueAsString(task);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New Task"));
    }

    @Test
    @WithMockUser
    void testCreateTaskValidationError() throws Exception {
        String json = "{\"title\":\"\"}";

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void testUpdateTaskSuccess() throws Exception {
        UUID id = UUID.randomUUID();
        Task task = Task.builder().title("Updated").status(Status.PENDING).build();
        when(taskService.updateTask(eq(id), any())).thenReturn(task);

        String json = objectMapper.writeValueAsString(task);

        mockMvc.perform(put("/tasks/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated"));
    }

    @Test
    @WithMockUser
    void testUpdateTaskInvalidStatus() throws Exception {
        UUID id = UUID.randomUUID();
        Task task = Task.builder().title("Updated").status(Status.PENDING).build();
        when(taskService.updateTask(eq(id), any()))
                .thenThrow(new InvalidStatusException("Cannot update"));

        String json = objectMapper.writeValueAsString(task);

        mockMvc.perform(put("/tasks/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Cannot update"));
    }

    @Test
    @WithMockUser
    void testDeleteTask() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(taskService).deleteTask(id);

        mockMvc.perform(delete("/tasks/{id}", id))
                .andExpect(status().isNoContent());

        verify(taskService).deleteTask(id);
    }

    @Test
    @WithMockUser
    void testGetAllTasksWithoutStatus() throws Exception {
        Page<Task> page = new PageImpl<>(List.of(Task.builder().title("Task1").status(Status.PENDING).build()));
        when(taskService.getAllTasks(any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Task1"));
    }

    @Test
    @WithMockUser
    void testGetAllTasksWithStatus() throws Exception {
        Page<Task> page = new PageImpl<>(List.of(Task.builder().title("Task2").status(Status.PENDING).build()));
        when(taskService.getAllTasks(eq(Status.PENDING), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/tasks").param("status", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Task2"));
    }
}

