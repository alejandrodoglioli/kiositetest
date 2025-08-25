package com.kiosite.kiositetest.controller.dto;

import com.kiosite.kiositetest.entity.Status;
import com.kiosite.kiositetest.entity.Task;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponseDTO {
    private String title;
    private String description;
    private Status status;
    private String createdBy;
    private LocalDateTime createdAt;
    private String modifiedBy;
    private LocalDateTime updatedAt;


    /**
     * Given a Task transform to TaskResponseDTO
     * @param task
     * @return
     */
    public static TaskResponseDTO fromEntity(Task task) {
        return new TaskResponseDTO(task.getTitle(), task.getDescription(), task.getStatus(), task.getCreatedBy(), task.getCreatedAt(), task.getModifiedBy(), task.getUpdatedAt());
    }
}