package com.systemdmove.controller;

import com.systemdmove.dto.WorkspaceDtos.*;
import com.systemdmove.service.WorkspaceService;
import com.systemdmove.session.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workspaces")
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    public WorkspaceController(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    @GetMapping
    public List<WorkspaceDto> list() {
        return workspaceService.listForUser(SecurityUtil.currentUserId());
    }

    @PostMapping
    public WorkspaceDto create(@Valid @RequestBody WorkspaceRequest req) {
        return workspaceService.create(req, SecurityUtil.currentUserId());
    }

    @PutMapping("/{id}")
    public WorkspaceDto update(@PathVariable Long id, @Valid @RequestBody WorkspaceRequest req) {
        return workspaceService.update(id, req, SecurityUtil.currentUserId());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        workspaceService.delete(id, SecurityUtil.currentUserId());
    }

    @GetMapping("/{id}/members")
    public List<MemberDto> members(@PathVariable Long id) {
        return workspaceService.listMembers(id, SecurityUtil.currentUserId());
    }

    @PostMapping("/{id}/members")
    public MemberDto invite(@PathVariable Long id, @Valid @RequestBody InviteRequest req) {
        return workspaceService.invite(id, req, SecurityUtil.currentUserId());
    }
}
