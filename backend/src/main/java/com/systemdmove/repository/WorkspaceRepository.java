package com.systemdmove.repository;

import com.systemdmove.model.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {

    /** Workspaces em que o usuario e membro (inclui os que ele e dono). */
    @Query("""
            SELECT w FROM Workspace w
            WHERE w.id IN (
                SELECT m.id.workspaceId FROM WorkspaceMember m WHERE m.id.userId = :userId
            )
            ORDER BY w.createdAt ASC
            """)
    List<Workspace> findAllForUser(@Param("userId") Long userId);
}
