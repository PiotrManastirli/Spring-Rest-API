package md.spring.restapi.task.tracker.store.repositories;

import md.spring.restapi.task.tracker.store.entities.TaskEntity;
import md.spring.restapi.task.tracker.store.entities.TaskStateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface TaskRepository  extends JpaRepository<TaskEntity,Long> {

    Optional<TaskEntity> findTaskEntityByTaskStateIdAndNameContainsIgnoreCase
            (Long TaskStateId, String taskStateName);
}
