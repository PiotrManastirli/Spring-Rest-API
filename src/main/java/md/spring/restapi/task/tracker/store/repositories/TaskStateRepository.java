package md.spring.restapi.task.tracker.store.repositories;

import md.spring.restapi.task.tracker.store.entities.TaskStateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskStateRepository extends JpaRepository<TaskStateEntity,Long> {
    Optional<TaskStateEntity> findTaskStateEntityByProjectAndNameContainsIgnoreCase(Long projectId,String taskStateName);

}
