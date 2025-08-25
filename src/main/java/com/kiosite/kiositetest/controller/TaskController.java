package com.kiosite.kiositetest.controller;

import com.kiosite.kiositetest.controller.dto.TaskRequestDTO;
import com.kiosite.kiositetest.controller.dto.TaskResponseDTO;
import com.kiosite.kiositetest.entity.Status;
import com.kiosite.kiositetest.entity.Task;
import com.kiosite.kiositetest.exception.InvalidStatusException;
import com.kiosite.kiositetest.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller for managing tasks.
 * Provides endpoints to create, read, update, delete, and filter tasks.
 */
@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "CRUD API for Task management")
public class TaskController {

    private final TaskService taskService;

    /**
     * Get a paginated list of tasks, optionally filtered by status.
     *
     * @param status Optional task status to filter (PENDING, IN_PROGRESS, DONE)
     * @param page   Page number, starts from 0
     * @param size   Number of tasks per page
     * @param sort   Field to sort by (e.g., title, createdAt)
     * @return Paginated list of TaskResponseDTO
     */
    @GetMapping
    @Operation(summary = "List all tasks with optional pagination and status filter")
    public ResponseEntity<Page<TaskResponseDTO>> getAllTasks(
            @Parameter(description = "Optional status filter (PENDING, IN_PROGRESS, DONE)")
            @RequestParam(required = false) Status status,
            @Parameter(description = "Page number, starts from 0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field, e.g., title, createdAt")
            @RequestParam(defaultValue = "createdAt") String sort
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending());
        Page<Task> tasks = taskService.getAllTasks(status, pageable);
        return ResponseEntity.ok(tasks.map(TaskResponseDTO::fromEntity));
    }

    /**
     * Create a new task.
     *
     * @param taskRequestDTO Task data to create
     * @return The created Task entity
     */
    @PostMapping
    @Operation(summary = "Create a new task")
    public ResponseEntity<Task> createTask(@Valid @RequestBody TaskRequestDTO taskRequestDTO) {
        Task created = taskService.createTask(taskRequestDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * Get a task by its ID.
     *
     * @param id Task UUID
     * @return TaskResponseDTO of the found task
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get a task by ID")
    public ResponseEntity<TaskResponseDTO> getTaskById(
            @Parameter(description = "UUID of the task to retrieve") @PathVariable UUID id
    ) {
        Task task = taskService.getTaskById(id);
        return ResponseEntity.ok(TaskResponseDTO.fromEntity(task));
    }

    /**
     * Update an existing task.
     *
     * @param id             Task UUID
     * @param taskRequestDTO Updated task data
     * @return Updated TaskResponseDTO
     * @throws InvalidStatusException If the provided status is invalid
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update a task")
    public ResponseEntity<TaskResponseDTO> updateTask(
            @Parameter(description = "UUID of the task to update") @PathVariable UUID id,
            @Valid @RequestBody TaskRequestDTO taskRequestDTO
    ) throws InvalidStatusException {
        Task updated = taskService.updateTask(id, taskRequestDTO);
        return ResponseEntity.ok(TaskResponseDTO.fromEntity(updated));
    }

    /**
     * Delete a task by its ID.
     *
     * @param id Task UUID
     * @return 204 No Content on success
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a task")
    public ResponseEntity<Void> deleteTask(
            @Parameter(description = "UUID of the task to delete") @PathVariable UUID id
    ) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}