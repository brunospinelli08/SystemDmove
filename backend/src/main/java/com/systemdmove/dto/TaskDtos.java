package com.systemdmove.dto;

import com.systemdmove.model.enums.Priority;
import com.systemdmove.model.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;
import java.util.List;

public final class TaskDtos {

    private TaskDtos() {
    }

    public record TaskCreateRequest(
            @NotBlank String title,
            String description,
            TaskStatus status,
            Priority priority,
            Instant dueDate,
            Long assigneeId,
            Long parentId
    ) {
    }

    public record TaskUpdateRequest(
            String title,
            String description,
            TaskStatus status,
            Priority priority,
            Instant dueDate,
            Long assigneeId
    ) {
    }

    /** Usado no drag-and-drop: novo status (coluna) e nova posicao (indice). */
    public record TaskMoveRequest(
            TaskStatus status,
            Integer position
    ) {
    }

    public record TagSummary(
            Long id,
            String name,
            String color
    ) {
    }

    public record TaskDto(
            Long id,
            Long listId,
            String title,
            String description,
            TaskStatus status,
            Priority priority,
            Instant dueDate,
            Long assigneeId,
            String assigneeName,
            Long createdById,
            Long parentId,
            Integer position,
            List<TagSummary> tags,
            Instant createdAt,
            Instant updatedAt
    ) {
    }
}
