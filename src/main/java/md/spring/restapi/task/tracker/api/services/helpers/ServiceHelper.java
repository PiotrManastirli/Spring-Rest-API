package md.spring.restapi.task.tracker.api.services.helpers;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import md.spring.restapi.task.tracker.api.exceptions.NotFoundException;
import md.spring.restapi.task.tracker.store.entities.ProjectEntity;
import md.spring.restapi.task.tracker.store.entities.TaskEntity;
import md.spring.restapi.task.tracker.store.entities.TaskStateEntity;
import md.spring.restapi.task.tracker.store.repositories.ProjectRepository;
import md.spring.restapi.task.tracker.store.repositories.TaskRepository;
import md.spring.restapi.task.tracker.store.repositories.TaskStateRepository;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
@Transactional
public class ServiceHelper {
    ProjectRepository projectRepository;
    TaskStateRepository taskStateRepository;
    TaskRepository taskRepository;
    public ProjectEntity getProjectOrThrowException(Long projectId) {
        return projectRepository
                .findById(projectId)
                .orElseThrow(() -> new NotFoundException(String.format("Project with \"%s\" doesn't exists.",
                        projectId))
                );
    }

    public TaskStateEntity getTaskStateOrThrowException(Long taskStateId) {
        return taskStateRepository
                .findById(taskStateId)
                .orElseThrow(() -> new NotFoundException(String.format("Project with \"%s\" doesn't exists.",
                        taskStateId))
                );
    }

    public TaskEntity getTaskOrThrowException(Long taskId) {
        return taskRepository
                .findById(taskId)
                .orElseThrow(() -> new NotFoundException(String.format("Project with \"%s\" doesn't exists.",
                        taskId))
                );
    }

}
