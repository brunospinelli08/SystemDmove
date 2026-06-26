package com.systemdmove.controller;

import com.systemdmove.dto.TaskDtos.*;
import com.systemdmove.service.TaskService;
import com.systemdmove.session.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/lists/{listId}/tasks")
    public List<TaskDto> listByList(@PathVariable Long listId,
                                    @RequestParam(value = "search", required = false) String search) {
        Long userId = SecurityUtil.currentUserId();
        if (search != null && !search.isBlank()) {
            return taskService.search(listId, search, userId);
        }
        return taskService.listByList(listId, userId);
    }

    @PostMapping("/lists/{listId}/tasks")
    public TaskDto create(@PathVariable Long listId, @Valid @RequestBody TaskCreateRequest req) {
        return taskService.create(listId, req, SecurityUtil.currentUserId());
    }

    @GetMapping("/tasks/{id}")
    public TaskDto get(@PathVariable Long id) {
        return taskService.get(id, SecurityUtil.currentUserId());
    }

    @PutMapping("/tasks/{id}")
    public TaskDto update(@PathVariable Long id, @RequestBody TaskUpdateRequest req) {
        return taskService.update(id, req, SecurityUtil.currentUserId());
    }

    @PatchMapping("/tasks/{id}/move")
    public TaskDto move(@PathVariable Long id, @RequestBody TaskMoveRequest req) {
        return taskService.move(id, req, SecurityUtil.currentUserId());
    }

    @DeleteMapping("/tasks/{id}")
    public void delete(@PathVariable Long id) {
        taskService.delete(id, SecurityUtil.currentUserId());
    }
}
