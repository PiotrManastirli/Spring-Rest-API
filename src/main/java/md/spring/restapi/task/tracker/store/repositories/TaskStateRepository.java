package md.spring.restapi.task.tracker.store.repositories;

import md.spring.restapi.task.tracker.store.entities.TaskStateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskStateRepository extends JpaRepository<TaskStateEntity,Long> {
}
