package md.spring.restapi.task.tracker.api.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import md.spring.restapi.task.tracker.api.dto.AckDto;
import md.spring.restapi.task.tracker.api.dto.TaskStateDto;
import md.spring.restapi.task.tracker.api.exceptions.BadRequestException;
import md.spring.restapi.task.tracker.api.exceptions.NotFoundException;
import md.spring.restapi.task.tracker.api.factories.TaskStateDtoFactory;
import md.spring.restapi.task.tracker.api.services.helpers.ServiceHelper;
import md.spring.restapi.task.tracker.store.entities.ProjectEntity;
import md.spring.restapi.task.tracker.store.entities.TaskStateEntity;
import md.spring.restapi.task.tracker.store.repositories.TaskStateRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
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
                .findTaskStateEntityByProjectIdAndNameContainsIgnoreCase(
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

    public TaskStateDto changeTaskPosition(Long taskStateId, Optional<Long> optionalLeftTaskStateId) {

        TaskStateEntity changeTaskState = getTaskStateOrThrowExceptions(taskStateId);

        ProjectEntity project = changeTaskState.getProject();

        Optional<Long> optionalOldLeftTaskStateId = changeTaskState
                .getLeftTaskState()
                .map(TaskStateEntity::getId);

        if (optionalOldLeftTaskStateId.equals(optionalLeftTaskStateId)) {
            return taskStateDtoFactory.makeTaskStateDto(changeTaskState);
        }

        Optional<TaskStateEntity> optionalNewLeftTaskState = optionalLeftTaskStateId
                .map(leftTaskStateId -> {

                    if (taskStateId.equals(leftTaskStateId)) {
                        throw new BadRequestException("Left task state id equals changed task state.");
                    }

                    TaskStateEntity leftTaskStateEntity = getTaskStateOrThrowExceptions(leftTaskStateId);

                    if (!project.getId().equals(leftTaskStateEntity.getProject().getId())) {
                        throw new BadRequestException("Task state position can be changed within the same project.");
                    }

                    return leftTaskStateEntity;
                });

        Optional<TaskStateEntity> optionalNewRightTaskState;
        if (!optionalNewLeftTaskState.isPresent()) {

            optionalNewRightTaskState = project
                    .getTaskStates()
                    .stream()
                    .filter(anotherTaskState -> !anotherTaskState.getLeftTaskState().isPresent())
                    .findAny();
        } else {

            optionalNewRightTaskState = optionalNewLeftTaskState
                    .get()
                    .getRightTaskState();
        }

        replaceOldTaskStatePosition(changeTaskState);

        if (optionalNewLeftTaskState.isPresent()) {

            TaskStateEntity newLeftTaskState = optionalNewLeftTaskState.get();

            newLeftTaskState.setRightTaskState(changeTaskState);

            changeTaskState.setLeftTaskState(newLeftTaskState);
        } else {
            changeTaskState.setLeftTaskState(null);
        }

        if (optionalNewRightTaskState.isPresent()) {

            TaskStateEntity newRightTaskState = optionalNewRightTaskState.get();

            newRightTaskState.setLeftTaskState(changeTaskState);

            changeTaskState.setRightTaskState(newRightTaskState);
        } else {
            changeTaskState.setRightTaskState(null);
        }

        changeTaskState = taskStateRepository.saveAndFlush(changeTaskState);

        optionalNewLeftTaskState
                .ifPresent(taskStateRepository::saveAndFlush);

        optionalNewRightTaskState
                .ifPresent(taskStateRepository::saveAndFlush);

        return taskStateDtoFactory.makeTaskStateDto(changeTaskState);
    }

    public AckDto deleteTaskState(Long taskStateId){
        TaskStateEntity changeTaskState = getTaskStateOrThrowExceptions(taskStateId);
        replaceOldTaskStatePosition(changeTaskState);
        taskStateRepository.delete(changeTaskState);
        return AckDto.builder().answer(true).build();
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

    private void replaceOldTaskStatePosition(TaskStateEntity changeTaskState) {

        Optional<TaskStateEntity> optionalOldLeftTaskState = changeTaskState.getLeftTaskState();
        Optional<TaskStateEntity> optionalOldRightTaskState = changeTaskState.getRightTaskState();

        optionalOldLeftTaskState
                .ifPresent(it -> {

                    it.setRightTaskState(optionalOldRightTaskState.orElse(null));

                    taskStateRepository.saveAndFlush(it);
                });

        optionalOldRightTaskState
                .ifPresent(it -> {

                    it.setLeftTaskState(optionalOldLeftTaskState.orElse(null));

                    taskStateRepository.saveAndFlush(it);
                });
    }

}
