package com.systemdmove.service;

import com.systemdmove.dto.HierarchyDtos.*;
import com.systemdmove.exception.ApiException;
import com.systemdmove.model.Folder;
import com.systemdmove.model.Space;
import com.systemdmove.model.TaskList;
import com.systemdmove.repository.FolderRepository;
import com.systemdmove.repository.TaskListRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListService {

    private final TaskListRepository listRepository;
    private final FolderRepository folderRepository;
    private final SpaceService spaceService;
    private final AccessService accessService;

    public ListService(TaskListRepository listRepository,
                       FolderRepository folderRepository,
                       SpaceService spaceService,
                       AccessService accessService) {
        this.listRepository = listRepository;
        this.folderRepository = folderRepository;
        this.spaceService = spaceService;
        this.accessService = accessService;
    }

    @Transactional(readOnly = true)
    public List<TaskListDto> listBySpace(Long spaceId, Long userId) {
        spaceService.getWithAccess(spaceId, userId);
        return listRepository.findBySpaceIdOrderByIdAsc(spaceId).stream()
                .map(this::toDto).toList();
    }

    @Transactional
    public TaskListDto create(Long spaceId, TaskListRequest req, Long userId) {
        Space space = spaceService.getWithAccess(spaceId, userId);
        TaskList list = new TaskList();
        list.setSpace(space);
        list.setName(req.name());
        if (req.folderId() != null) {
            Folder folder = folderRepository.findById(req.folderId())
                    .orElseThrow(() -> ApiException.notFound("Folder nao encontrado"));
            if (!folder.getSpace().getId().equals(spaceId)) {
                throw ApiException.badRequest("O folder nao pertence a este space");
            }
            list.setFolder(folder);
        }
        return toDto(listRepository.save(list));
    }

    @Transactional
    public TaskListDto update(Long listId, TaskListRequest req, Long userId) {
        TaskList list = getWithAccess(listId, userId);
        list.setName(req.name());
        return toDto(list);
    }

    @Transactional
    public void delete(Long listId, Long userId) {
        TaskList list = getWithAccess(listId, userId);
        listRepository.delete(list);
    }

    public TaskList getWithAccess(Long listId, Long userId) {
        TaskList list = listRepository.findById(listId)
                .orElseThrow(() -> ApiException.notFound("List nao encontrada"));
        accessService.requireMember(list.getSpace().getWorkspace().getId(), userId);
        return list;
    }

    private TaskListDto toDto(TaskList l) {
        Long folderId = l.getFolder() != null ? l.getFolder().getId() : null;
        return new TaskListDto(l.getId(), l.getSpace().getId(), folderId, l.getName());
    }
}
