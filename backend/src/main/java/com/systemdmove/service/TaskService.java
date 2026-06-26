package com.systemdmove.service;

import com.systemdmove.dto.TaskDtos.*;
import com.systemdmove.exception.ApiException;
import com.systemdmove.model.Task;
import com.systemdmove.model.TaskList;
import com.systemdmove.model.User;
import com.systemdmove.model.enums.Priority;
import com.systemdmove.model.enums.TaskStatus;
import com.systemdmove.repository.TaskRepository;
import com.systemdmove.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ListService listService;

    public TaskService(TaskRepository taskRepository,
                       UserRepository userRepository,
                       ListService listService) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.listService = listService;
    }

    @Transactional(readOnly = true)
    public List<TaskDto> listByList(Long listId, Long userId) {
        listService.getWithAccess(listId, userId);
        return taskRepository.findByListIdOrderByPositionAscIdAsc(listId).stream()
                .map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<TaskDto> search(Long listId, String term, Long userId) {
        listService.getWithAccess(listId, userId);
        return taskRepository.searchByTitleInList(listId, term).stream()
                .map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public TaskDto get(Long taskId, Long userId) {
        return toDto(getWithAccess(taskId, userId));
    }

    @Transactional
    public TaskDto create(Long listId, TaskCreateRequest req, Long userId) {
        TaskList list = listService.getWithAccess(listId, userId);
        User creator = userRepository.findById(userId)
                .orElseThrow(() -> ApiException.notFound("Usuario nao encontrado"));

        Task task = new Task();
        task.setList(list);
        task.setTitle(req.title());
        task.setDescription(req.description());
        task.setStatus(req.status() != null ? req.status() : TaskStatus.TO_DO);
        task.setPriority(req.priority() != null ? req.priority() : Priority.NORMAL);
        task.setDueDate(req.dueDate());
        task.setCreatedBy(creator);
        task.setPosition(taskRepository.findMaxPositionInList(listId) + 1);

        if (req.assigneeId() != null) {
            task.setAssignee(resolveUser(req.assigneeId()));
        }
        if (req.parentId() != null) {
            Task parent = taskRepository.findById(req.parentId())
                    .orElseThrow(() -> ApiException.notFound("Tarefa pai nao encontrada"));
            task.setParent(parent);
        }
        return toDto(taskRepository.save(task));
    }

    @Transactional
    public TaskDto update(Long taskId, TaskUpdateRequest req, Long userId) {
        Task task = getWithAccess(taskId, userId);
        if (req.title() != null) task.setTitle(req.title());
        if (req.description() != null) task.setDescription(req.description());
        if (req.status() != null) task.setStatus(req.status());
        if (req.priority() != null) task.setPriority(req.priority());
        task.setDueDate(req.dueDate());
        task.setAssignee(req.assigneeId() != null ? resolveUser(req.assigneeId()) : null);
        return toDto(task);
    }

    @Transactional
    public void delete(Long taskId, Long userId) {
        Task task = getWithAccess(taskId, userId);
        taskRepository.delete(task);
    }

    /**
     * Move/reordena a tarefa (drag-and-drop). Define o novo status (coluna) e
     * recoloca a tarefa na posicao indicada dentro da coluna de destino,
     * renumerando as posicoes da coluna com espacamento de 10.
     */
    @Transactional
    public TaskDto move(Long taskId, TaskMoveRequest req, Long userId) {
        Task task = getWithAccess(taskId, userId);
        Long listId = task.getList().getId();
        TaskStatus targetStatus = req.status() != null ? req.status() : task.getStatus();
        task.setStatus(targetStatus);

        // Irmaos da coluna de destino (mesmo status), exceto a propria tarefa.
        List<Task> column = new ArrayList<>(taskRepository.findByListIdOrderByPositionAscIdAsc(listId).stream()
                .filter(t -> t.getStatus() == targetStatus && !t.getId().equals(taskId))
                .toList());

        int index = req.position() != null ? Math.max(0, Math.min(req.position(), column.size())) : column.size();
        column.add(index, task);

        for (int i = 0; i < column.size(); i++) {
            column.get(i).setPosition(i * 10);
        }
        return toDto(task);
    }

    public Task getWithAccess(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> ApiException.notFound("Tarefa nao encontrada"));
        listService.getWithAccess(task.getList().getId(), userId);
        return task;
    }

    private User resolveUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> ApiException.badRequest("Responsavel invalido"));
    }

    private TaskDto toDto(Task t) {
        List<TagSummary> tags = t.getTags().stream()
                .map(tag -> new TagSummary(tag.getId(), tag.getName(), tag.getColor()))
                .toList();
        return new TaskDto(
                t.getId(),
                t.getList().getId(),
                t.getTitle(),
                t.getDescription(),
                t.getStatus(),
                t.getPriority(),
                t.getDueDate(),
                t.getAssignee() != null ? t.getAssignee().getId() : null,
                t.getAssignee() != null ? t.getAssignee().getName() : null,
                t.getCreatedBy().getId(),
                t.getParent() != null ? t.getParent().getId() : null,
                t.getPosition(),
                tags,
                t.getCreatedAt(),
                t.getUpdatedAt()
        );
    }
}
