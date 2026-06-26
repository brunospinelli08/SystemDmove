package com.systemdmove.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTOs de spaces, folders e lists.
 */
public final class HierarchyDtos {

    private HierarchyDtos() {
    }

    // ===== Space =====
    public record SpaceRequest(
            @NotBlank String name,
            String color,
            String icon
    ) {
    }

    public record SpaceDto(
            Long id,
            Long workspaceId,
            String name,
            String color,
            String icon
    ) {
    }

    // ===== Folder =====
    public record FolderRequest(
            @NotBlank String name
    ) {
    }

    public record FolderDto(
            Long id,
            Long spaceId,
            String name
    ) {
    }

    // ===== List =====
    public record TaskListRequest(
            @NotBlank String name,
            Long folderId
    ) {
    }

    public record TaskListDto(
            Long id,
            Long spaceId,
            Long folderId,
            String name
    ) {
    }
}
