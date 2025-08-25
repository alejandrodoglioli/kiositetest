package com.kiosite.kiositetest.service;

import com.kiosite.kiositetest.controller.dto.TaskRequestDTO;
import com.kiosite.kiositetest.entity.Status;
import com.kiosite.kiositetest.entity.Task;
import com.kiosite.kiositetest.exception.InvalidStatusException;
import com.kiosite.kiositetest.exception.NotFoundException;
import com.kiosite.kiositetest.repository.TaskRepository;
import com.kiosite.kiositetest.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    private UUID taskId;
    private Task task;
    private TaskRequestDTO taskRequestDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        taskId = UUID.randomUUID();
        task = Task.builder()
                .id(taskId)
                .title("Title")
                .description("Description")
                .status(Status.PENDING)
                .build();

        taskRequestDTO = new TaskRequestDTO();
        taskRequestDTO.setTitle("New Title");
        taskRequestDTO.setDescription("New Description");
        taskRequestDTO.setStatus(Status.DONE);
    }

    @Test
    void createTask_savesTaskWithProvidedStatus() {
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task result = taskService.createTask(taskRequestDTO);

        assertNotNull(result);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void createTask_savesTaskWithDefaultStatusIfNull() {
        taskRequestDTO.setStatus(null);
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task result = taskService.createTask(taskRequestDTO);

        assertEquals(Status.PENDING, result.getStatus());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void getAllTasks_returnsPagedTasks() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> page = new PageImpl<>(List.of(task));
        when(taskRepository.findAll(pageable)).thenReturn(page);

        Page<Task> result = taskService.getAllTasks(null, pageable);

        assertEquals(1, result.getTotalElements());
        verify(taskRepository, times(1)).findAll(pageable);
    }

    @Test
    void getTaskById_whenExists_returnsTask() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        Task result = taskService.getTaskById(taskId);

        assertEquals(taskId, result.getId());
        verify(taskRepository, times(1)).findById(taskId);
    }

    @Test
    void getTaskById_whenNotExists_throwsNotFoundException() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> taskService.getTaskById(taskId));
        verify(taskRepository, times(1)).findById(taskId);
    }

    @Test
    void updateTask_updatesFieldsAndStatus() throws InvalidStatusException {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task result = taskService.updateTask(taskId, taskRequestDTO);

        assertEquals("New Title", result.getTitle());
        assertEquals("New Description", result.getDescription());
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    void updateTask_invalidStatus_throwsException() {
        task.setStatus(Status.IN_PROGRESS);
        taskRequestDTO.setStatus(Status.DONE);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        assertThrows(InvalidStatusException.class, () -> taskService.updateTask(taskId, taskRequestDTO));
    }

    @Test
    void deleteTask_deletesExistingTask() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        doNothing().when(taskRepository).delete(task);

        taskService.deleteTask(taskId);

        verify(taskRepository, times(1)).delete(task);
    }

    @Test
    void getTasksByStatus_returnsPagedTasks() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> page = new PageImpl<>(List.of(task));
        when(taskRepository.findByStatus(Status.PENDING, pageable)).thenReturn(page);

        Page<Task> result = taskService.getAllTasks(Status.PENDING, pageable);

        assertEquals(1, result.getTotalElements());
        verify(taskRepository, times(1)).findByStatus(Status.PENDING, pageable);
    }
}
