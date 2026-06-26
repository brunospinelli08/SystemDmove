package com.systemdmove.repository;

import com.systemdmove.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByListIdOrderByPositionAscIdAsc(Long listId);

    List<Task> findByParentIdOrderByPositionAscIdAsc(Long parentId);

    /** Maior position dentro de uma list (para inserir no fim). */
    @Query("SELECT COALESCE(MAX(t.position), -1) FROM Task t WHERE t.list.id = :listId")
    int findMaxPositionInList(@Param("listId") Long listId);

    /** Tarefas atribuidas a um usuario (para dashboard). */
    List<Task> findByAssigneeIdOrderByDueDateAsc(Long assigneeId);

    /** Busca global por titulo dentro de uma list. */
    @Query("""
            SELECT t FROM Task t
            WHERE t.list.id = :listId
              AND LOWER(t.title) LIKE LOWER(CONCAT('%', :term, '%'))
            ORDER BY t.position ASC
            """)
    List<Task> searchByTitleInList(@Param("listId") Long listId, @Param("term") String term);
}
