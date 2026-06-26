package com.systemdmove.repository;

import com.systemdmove.model.TaskList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskListRepository extends JpaRepository<TaskList, Long> {
    List<TaskList> findBySpaceIdOrderByIdAsc(Long spaceId);
    List<TaskList> findByFolderIdOrderByIdAsc(Long folderId);
}
