package com.systemdmove.repository;

import com.systemdmove.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findByWorkspaceIdOrderByNameAsc(Long workspaceId);
}
