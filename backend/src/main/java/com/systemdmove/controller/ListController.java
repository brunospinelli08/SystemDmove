package com.systemdmove.controller;

import com.systemdmove.dto.HierarchyDtos.*;
import com.systemdmove.service.ListService;
import com.systemdmove.session.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ListController {

    private final ListService listService;

    public ListController(ListService listService) {
        this.listService = listService;
    }

    @GetMapping("/spaces/{spaceId}/lists")
    public List<TaskListDto> list(@PathVariable Long spaceId) {
        return listService.listBySpace(spaceId, SecurityUtil.currentUserId());
    }

    @PostMapping("/spaces/{spaceId}/lists")
    public TaskListDto create(@PathVariable Long spaceId, @Valid @RequestBody TaskListRequest req) {
        return listService.create(spaceId, req, SecurityUtil.currentUserId());
    }

    @PutMapping("/lists/{id}")
    public TaskListDto update(@PathVariable Long id, @Valid @RequestBody TaskListRequest req) {
        return listService.update(id, req, SecurityUtil.currentUserId());
    }

    @DeleteMapping("/lists/{id}")
    public void delete(@PathVariable Long id) {
        listService.delete(id, SecurityUtil.currentUserId());
    }
}
