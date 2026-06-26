package com.systemdmove.service;

import com.systemdmove.dto.HierarchyDtos.*;
import com.systemdmove.exception.ApiException;
import com.systemdmove.model.Folder;
import com.systemdmove.model.Space;
import com.systemdmove.repository.FolderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FolderService {

    private final FolderRepository folderRepository;
    private final SpaceService spaceService;
    private final AccessService accessService;

    public FolderService(FolderRepository folderRepository,
                         SpaceService spaceService,
                         AccessService accessService) {
        this.folderRepository = folderRepository;
        this.spaceService = spaceService;
        this.accessService = accessService;
    }

    @Transactional(readOnly = true)
    public List<FolderDto> listBySpace(Long spaceId, Long userId) {
        spaceService.getWithAccess(spaceId, userId);
        return folderRepository.findBySpaceIdOrderByIdAsc(spaceId).stream()
                .map(this::toDto).toList();
    }

    @Transactional
    public FolderDto create(Long spaceId, FolderRequest req, Long userId) {
        Space space = spaceService.getWithAccess(spaceId, userId);
        Folder folder = new Folder();
        folder.setSpace(space);
        folder.setName(req.name());
        return toDto(folderRepository.save(folder));
    }

    @Transactional
    public FolderDto update(Long folderId, FolderRequest req, Long userId) {
        Folder folder = getWithAccess(folderId, userId);
        folder.setName(req.name());
        return toDto(folder);
    }

    @Transactional
    public void delete(Long folderId, Long userId) {
        Folder folder = getWithAccess(folderId, userId);
        folderRepository.delete(folder);
    }

    public Folder getWithAccess(Long folderId, Long userId) {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> ApiException.notFound("Folder nao encontrado"));
        accessService.requireMember(folder.getSpace().getWorkspace().getId(), userId);
        return folder;
    }

    private FolderDto toDto(Folder f) {
        return new FolderDto(f.getId(), f.getSpace().getId(), f.getName());
    }
}
