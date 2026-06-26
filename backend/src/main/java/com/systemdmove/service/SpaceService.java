package com.systemdmove.service;

import com.systemdmove.dto.HierarchyDtos.*;
import com.systemdmove.exception.ApiException;
import com.systemdmove.model.Space;
import com.systemdmove.model.Workspace;
import com.systemdmove.repository.SpaceRepository;
import com.systemdmove.repository.WorkspaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SpaceService {

    private final SpaceRepository spaceRepository;
    private final WorkspaceRepository workspaceRepository;
    private final AccessService accessService;

    public SpaceService(SpaceRepository spaceRepository,
                        WorkspaceRepository workspaceRepository,
                        AccessService accessService) {
        this.spaceRepository = spaceRepository;
        this.workspaceRepository = workspaceRepository;
        this.accessService = accessService;
    }

    @Transactional(readOnly = true)
    public List<SpaceDto> listByWorkspace(Long workspaceId, Long userId) {
        accessService.requireMember(workspaceId, userId);
        return spaceRepository.findByWorkspaceIdOrderByIdAsc(workspaceId).stream()
                .map(this::toDto).toList();
    }

    @Transactional
    public SpaceDto create(Long workspaceId, SpaceRequest req, Long userId) {
        accessService.requireMember(workspaceId, userId);
        Workspace ws = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> ApiException.notFound("Workspace nao encontrado"));
        Space space = new Space();
        space.setWorkspace(ws);
        space.setName(req.name());
        space.setColor(req.color());
        space.setIcon(req.icon());
        return toDto(spaceRepository.save(space));
    }

    @Transactional
    public SpaceDto update(Long spaceId, SpaceRequest req, Long userId) {
        Space space = getWithAccess(spaceId, userId);
        space.setName(req.name());
        space.setColor(req.color());
        space.setIcon(req.icon());
        return toDto(space);
    }

    @Transactional
    public void delete(Long spaceId, Long userId) {
        Space space = getWithAccess(spaceId, userId);
        spaceRepository.delete(space);
    }

    /** Carrega o space e garante que o usuario e membro do workspace dono. */
    public Space getWithAccess(Long spaceId, Long userId) {
        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> ApiException.notFound("Space nao encontrado"));
        accessService.requireMember(space.getWorkspace().getId(), userId);
        return space;
    }

    private SpaceDto toDto(Space s) {
        return new SpaceDto(s.getId(), s.getWorkspace().getId(), s.getName(), s.getColor(), s.getIcon());
    }
}
