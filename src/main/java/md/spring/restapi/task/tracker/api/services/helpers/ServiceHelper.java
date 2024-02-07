package md.spring.restapi.task.tracker.api.services.helpers;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import md.spring.restapi.task.tracker.api.exceptions.NotFoundException;
import md.spring.restapi.task.tracker.store.entities.ProjectEntity;
import md.spring.restapi.task.tracker.store.repositories.ProjectRepository;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
@Transactional
public class ServiceHelper {
    ProjectRepository projectRepository;
    public ProjectEntity getProjectOrThrowException(Long projectId) {
        return projectRepository
                .findById(projectId)
                .orElseThrow(() -> new NotFoundException(String.format("Project with \"%s\" doesn't exists.",
                        projectId))
                );
    }

}
