package com.systemdmove.controller;

import com.systemdmove.dto.HierarchyDtos.*;
import com.systemdmove.service.SpaceService;
import com.systemdmove.session.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SpaceController {

    private final SpaceService spaceService;

    public SpaceController(SpaceService spaceService) {
        this.spaceService = spaceService;
    }

    @GetMapping("/workspaces/{workspaceId}/spaces")
    public List<SpaceDto> list(@PathVariable Long workspaceId) {
        return spaceService.listByWorkspace(workspaceId, SecurityUtil.currentUserId());
    }

    @PostMapping("/workspaces/{workspaceId}/spaces")
    public SpaceDto create(@PathVariable Long workspaceId, @Valid @RequestBody SpaceRequest req) {
        return spaceService.create(workspaceId, req, SecurityUtil.currentUserId());
    }

    @PutMapping("/spaces/{id}")
    public SpaceDto update(@PathVariable Long id, @Valid @RequestBody SpaceRequest req) {
        return spaceService.update(id, req, SecurityUtil.currentUserId());
    }

    @DeleteMapping("/spaces/{id}")
    public void delete(@PathVariable Long id) {
        spaceService.delete(id, SecurityUtil.currentUserId());
    }
}
