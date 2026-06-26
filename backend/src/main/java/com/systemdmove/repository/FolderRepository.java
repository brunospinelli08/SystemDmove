package com.systemdmove.repository;

import com.systemdmove.model.Folder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FolderRepository extends JpaRepository<Folder, Long> {
    List<Folder> findBySpaceIdOrderByIdAsc(Long spaceId);
}
