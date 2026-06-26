package com.systemdmove.controller;

import com.systemdmove.dto.HierarchyDtos.*;
import com.systemdmove.service.FolderService;
import com.systemdmove.session.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class FolderController {

    private final FolderService folderService;

    public FolderController(FolderService folderService) {
        this.folderService = folderService;
    }

    @GetMapping("/spaces/{spaceId}/folders")
    public List<FolderDto> list(@PathVariable Long spaceId) {
        return folderService.listBySpace(spaceId, SecurityUtil.currentUserId());
    }

    @PostMapping("/spaces/{spaceId}/folders")
    public FolderDto create(@PathVariable Long spaceId, @Valid @RequestBody FolderRequest req) {
        return folderService.create(spaceId, req, SecurityUtil.currentUserId());
    }

    @PutMapping("/folders/{id}")
    public FolderDto update(@PathVariable Long id, @Valid @RequestBody FolderRequest req) {
        return folderService.update(id, req, SecurityUtil.currentUserId());
    }

    @DeleteMapping("/folders/{id}")
    public void delete(@PathVariable Long id) {
        folderService.delete(id, SecurityUtil.currentUserId());
    }
}
