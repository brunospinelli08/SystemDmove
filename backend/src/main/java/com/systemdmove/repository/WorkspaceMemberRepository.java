package com.systemdmove.repository;

import com.systemdmove.model.WorkspaceMember;
import com.systemdmove.model.WorkspaceMemberId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkspaceMemberRepository extends JpaRepository<WorkspaceMember, WorkspaceMemberId> {

    List<WorkspaceMember> findByIdWorkspaceId(Long workspaceId);

    Optional<WorkspaceMember> findByIdWorkspaceIdAndIdUserId(Long workspaceId, Long userId);

    boolean existsByIdWorkspaceIdAndIdUserId(Long workspaceId, Long userId);
}
