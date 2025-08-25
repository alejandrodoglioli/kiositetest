package com.kiosite.kiositetest.service.impl;

import com.kiosite.kiositetest.controller.dto.TaskRequestDTO;
import com.kiosite.kiositetest.entity.Status;
import com.kiosite.kiositetest.entity.Task;
import com.kiosite.kiositetest.exception.InvalidStatusException;
import com.kiosite.kiositetest.exception.NotFoundException;
import com.kiosite.kiositetest.repository.TaskRepository;
import com.kiosite.kiositetest.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Implementation of {@link TaskService} that manages CRUD operations for tasks.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    /**
     * Create a new task with the given data.
     * <p>
     * If the status is not provided, defaults to {@link Status#PENDING}.
     *
     * @param taskRequestDTO DTO containing title, description, and optional status
     * @return The created {@link Task} entity
     */
    @Override
    public Task createTask(TaskRequestDTO taskRequestDTO) {
        Task task = Task.builder()
                .title(taskRequestDTO.getTitle())
                .description(taskRequestDTO.getDescription())
                .status(taskRequestDTO.getStatus() != null ? taskRequestDTO.getStatus() : Status.PENDING)
                .build();
        return taskRepository.save(task);
    }

    /**
     * Retrieves a paginated list of tasks, optionally filtered by status.
     *
     * @param status   Optional {@link Status} to filter tasks
     * @param pageable {@link Pageable} object containing page number, size, and sorting
     * @return A {@link Page} of {@link Task} entities
     */
    @Override
    public Page<Task> getAllTasks(Status status, Pageable pageable) {
        if (status != null) {
            return taskRepository.findByStatus(status, pageable);
        } else {
            return taskRepository.findAll(pageable);
        }
    }

    /**
     * Retrieves a task by UUID.
     *
     * @param id UUID of the task to retrieve
     * @return The found {@link Task} entity
     * @throws NotFoundException if no task exists with the given id
     */
    @Override
    public Task getTaskById(UUID id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Task not found with id: " + id));
    }

    /**
     * Updates an existing task with new data.
     * <p>
     * Business rules:
     * <ul>
     *     <li>Cannot mark a task as DONE if it is currently IN_PROGRESS.</li>
     * </ul>
     *
     * @param id             UUID of the task to update
     * @param taskRequestDTO DTO containing updated title, description, and optional status
     * @return The updated {@link Task} entity
     * @throws NotFoundException      if no task exists with the given id
     * @throws InvalidStatusException if attempting an invalid status transition
     */
    @Override
    public Task updateTask(UUID id, TaskRequestDTO taskRequestDTO) throws InvalidStatusException {
        Task task = getTaskById(id);

        task.setTitle(taskRequestDTO.getTitle());
        task.setDescription(taskRequestDTO.getDescription());

        if (task.getStatus() == Status.IN_PROGRESS && taskRequestDTO.getStatus() == Status.DONE) {
            throw new InvalidStatusException("Cannot mark task as DONE while it is IN_PROGRESS");
        }

        if (taskRequestDTO.getStatus() != null) {
            task.setStatus(taskRequestDTO.getStatus());
        }

        return taskRepository.save(task);
    }

    /**
     * Delete a task by UUID.
     *
     * @param id UUID of the task to delete
     * @throws NotFoundException if no task exists with the given id
     */
    @Override
    public void deleteTask(UUID id) {
        Task task = getTaskById(id);
        taskRepository.delete(task);
    }
}
