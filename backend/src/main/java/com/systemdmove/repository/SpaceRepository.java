package com.systemdmove.repository;

import com.systemdmove.model.Space;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpaceRepository extends JpaRepository<Space, Long> {
    List<Space> findByWorkspaceIdOrderByIdAsc(Long workspaceId);
}
