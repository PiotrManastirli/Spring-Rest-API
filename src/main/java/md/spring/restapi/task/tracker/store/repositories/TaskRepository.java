package md.spring.restapi.task.tracker.store.repositories;

import md.spring.restapi.task.tracker.store.entities.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository  extends JpaRepository<TaskEntity,Long> {
}
