package com.systemdmove.dto;

import jakarta.validation.constraints.NotBlank;

public final class WorkspaceDtos {

    private WorkspaceDtos() {
    }

    public record WorkspaceRequest(
            @NotBlank String name,
            String description
    ) {
    }

    public record WorkspaceDto(
            Long id,
            String name,
            String description,
            Long ownerId,
            String role
    ) {
    }

    public record MemberDto(
            Long userId,
            String name,
            String email,
            String role
    ) {
    }

    public record InviteRequest(
            @NotBlank String email,
            String role
    ) {
    }
}
