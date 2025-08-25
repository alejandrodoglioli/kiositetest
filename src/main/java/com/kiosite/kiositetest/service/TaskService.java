package com.kiosite.kiositetest.service;

import com.kiosite.kiositetest.controller.dto.TaskRequestDTO;
import com.kiosite.kiositetest.entity.Status;
import com.kiosite.kiositetest.entity.Task;
import com.kiosite.kiositetest.exception.InvalidStatusException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TaskService {

    Task createTask(TaskRequestDTO taskDTO);

    Page<Task> getAllTasks(Status status, Pageable pageable);

    Task getTaskById(UUID id);

    Task updateTask(UUID id, TaskRequestDTO taskDTO) throws InvalidStatusException;

    void deleteTask(UUID id);

}
