package md.spring.restapi.task.tracker.api.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import md.spring.restapi.task.tracker.api.dto.TaskStateDto;
import md.spring.restapi.task.tracker.api.exceptions.BadRequestException;
import md.spring.restapi.task.tracker.api.exceptions.NotFoundException;
import md.spring.restapi.task.tracker.api.factories.TaskStateDtoFactory;
import md.spring.restapi.task.tracker.api.services.helpers.ServiceHelper;
import md.spring.restapi.task.tracker.store.entities.ProjectEntity;
import md.spring.restapi.task.tracker.store.entities.TaskStateEntity;
import md.spring.restapi.task.tracker.store.repositories.TaskStateRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class TaskStateService {
    TaskStateRepository taskStateRepository;

    TaskStateDtoFactory taskStateDtoFactory;

    ServiceHelper serviceHelper;

    public List<TaskStateDto> getTaskStates(Long projectId){
       ProjectEntity project = serviceHelper.getProjectOrThrowException(projectId);
       return project
               .getTaskStates()
               .stream()
               .map(taskStateDtoFactory::makeTaskStateDto)
               .collect(Collectors.toList());
    }

    public TaskStateDto createTaskState(Long projectId, String taskStateName){
        if (taskStateName.trim().isEmpty()){
            throw new BadRequestException("Task state name can't be empty!");
        }
        ProjectEntity project = serviceHelper.getProjectOrThrowException(projectId);
        Optional<TaskStateEntity> optionalAnotherTaskState = Optional.empty();
        for(TaskStateEntity taskState: project.getTaskStates()){
           if(taskState.getName().equalsIgnoreCase(taskStateName)){
               throw new BadRequestException(String.format("Task state \"%s \"already exists",taskStateName));
           }
           if (!taskState.getRightTaskState().isPresent()) {
               optionalAnotherTaskState = Optional.of(taskState);
               break;
           }
        }

        TaskStateEntity taskState = taskStateRepository.saveAndFlush(
                TaskStateEntity.builder()
                .name(taskStateName)
                .project(project)
                .build()
                );

        optionalAnotherTaskState
                .ifPresent(anotherTaskState -> {
                    taskState.setLeftTaskState(anotherTaskState);
                    anotherTaskState.setRightTaskState(taskState);

                    taskStateRepository.saveAndFlush(anotherTaskState);
                });

        final TaskStateEntity savedTaskState = taskStateRepository.saveAndFlush(taskState);
        return taskStateDtoFactory.makeTaskStateDto(savedTaskState);
    }

    public TaskStateDto updateTaskState(Long taskStateId, String taskStateName) {
        if (taskStateName.trim().isEmpty()){
            throw new BadRequestException("Task state name can't be empty!");
        }

        TaskStateEntity taskState = getTaskStateOrThrowExceptions(taskStateId);

        taskStateRepository
                .findTaskStateEntityByProjectAndNameContainsIgnoreCase(
                        taskState.getProject().getId(),
                        taskStateName)
                .filter(anotherTaskState -> !anotherTaskState.getId().equals(taskStateId))
                .ifPresent(anotherTaskState -> {
                    throw new BadRequestException(String.format("Task State \"%s\" already exists.",taskStateName));
                });
        taskState.setName(taskStateName);
        taskState = taskStateRepository.saveAndFlush(taskState);
        return taskStateDtoFactory.makeTaskStateDto(taskState);
    }

    private TaskStateEntity getTaskStateOrThrowExceptions(Long taskStateId){
        return taskStateRepository
                .findById(taskStateId)
                .orElseThrow(
                        ()-> new NotFoundException(
                                String.format("Task state with \"%s\" id doesn't exist.",
                                        taskStateId)
                        )
                );
    }

}
