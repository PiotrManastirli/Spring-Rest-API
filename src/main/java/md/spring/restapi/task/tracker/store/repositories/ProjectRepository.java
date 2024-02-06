package md.spring.restapi.task.tracker.store.repositories;

import md.spring.restapi.task.tracker.store.entities.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<ProjectEntity,Long> {
    Optional<ProjectEntity> findByName(String name);
}
