package com.kiosite.kiositetest.repository;

import com.kiosite.kiositetest.entity.Status;
import com.kiosite.kiositetest.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    Page<Task> findByStatus(Status status, Pageable pageable);
}