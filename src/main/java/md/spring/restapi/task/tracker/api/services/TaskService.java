package md.spring.restapi.task.tracker.api.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import md.spring.restapi.task.tracker.api.dto.AckDto;
import md.spring.restapi.task.tracker.api.dto.TaskDto;
import md.spring.restapi.task.tracker.api.dto.TaskStateDto;
import md.spring.restapi.task.tracker.api.exceptions.BadRequestException;
import md.spring.restapi.task.tracker.api.exceptions.NotFoundException;
import md.spring.restapi.task.tracker.api.factories.TaskDtoFactory;
import md.spring.restapi.task.tracker.api.services.helpers.ServiceHelper;
import md.spring.restapi.task.tracker.store.entities.ProjectEntity;
import md.spring.restapi.task.tracker.store.entities.TaskEntity;
import md.spring.restapi.task.tracker.store.entities.TaskStateEntity;
import md.spring.restapi.task.tracker.store.entities.UserEntity;
import md.spring.restapi.task.tracker.store.repositories.ProjectRepository;
import md.spring.restapi.task.tracker.store.repositories.TaskRepository;
import md.spring.restapi.task.tracker.store.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class TaskService {
    ProjectRepository projectRepository;
    TaskRepository taskRepository;

    UserRepository userRepository;

    TaskDtoFactory taskDtoFactory;

    ServiceHelper serviceHelper;

    public List<TaskDto> getUserTasks(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return user.getTasks()
                .stream()
                .map(taskDtoFactory::makeTaskDto)
                .collect(Collectors.toList());
    }

    public List<TaskDto> getTasksByProject(Long projectId) {
        ProjectEntity project = serviceHelper.getProjectOrThrowException(projectId);
        return project
                .getTaskStates()
                .stream()
                .flatMap(taskState -> taskState
                        .getTasks()
                        .stream()
                        .map(taskDtoFactory::makeTaskDto))
                .collect(Collectors.toList());
    }


    public List<TaskDto> getTasks(Long taskStateId){
        TaskStateEntity taskState = serviceHelper.getTaskStateOrThrowException(taskStateId);
        return taskState
                .getTasks()
                .stream()
                .map(taskDtoFactory::makeTaskDto)
                .collect(Collectors.toList());
    }

    public TaskDto createTask(Long taskStateId, String taskName, String taskDescription, Long userId){
        if (taskName.trim().isEmpty()){
            throw new BadRequestException("Task name can't be empty!");
        }
        TaskStateEntity taskState = serviceHelper.getTaskStateOrThrowException(taskStateId);

        if (taskState
                .getTasks()
                .stream()
                .anyMatch(task ->
                        task.getName().equalsIgnoreCase(taskName))) {
            throw new BadRequestException(String.format("Task \"%s\" already exists", taskName));
        }
        TaskEntity task = TaskEntity.builder()
                .name(taskName)
                .description(taskDescription)
                .taskState(taskState)
                .user(serviceHelper.getUserOrThrowException(userId))
                .build();

        TaskEntity savedTask = taskRepository.saveAndFlush(task);

        return taskDtoFactory.makeTaskDto(savedTask);
    }

    public TaskDto updateTask(Long taskId,
                                   Optional<String> taskName,
                                   Optional<String> taskDescription) {
        TaskEntity task = serviceHelper.getTaskOrThrowException(taskId);

        if (taskName.isPresent() && !taskName.get().trim().isEmpty()) {
            String newTaskName = taskName.get();
            taskRepository.findTaskEntityByTaskStateIdAndNameContainsIgnoreCase(
                            task.getTaskState().getId(),
                            newTaskName)
                    .filter(anotherTaskState -> !anotherTaskState.getId().equals(taskId))
                    .ifPresent(anotherTaskState -> {
                        throw new BadRequestException(String.format("Task \"%s\" already exists.", newTaskName));
                    });

            task.setName(newTaskName);
        }
        if (taskDescription.isPresent() && !taskDescription.get().trim().isEmpty()) {
            String newTaskDescription = taskDescription.get();
            task.setDescription(newTaskDescription);
        }
        task = taskRepository.saveAndFlush(task);
        return taskDtoFactory.makeTaskDto(task);
    }

    public TaskDto changeTaskPosition(Long taskId, Long taskStateId) {
        TaskEntity task = serviceHelper.getTaskOrThrowException(taskId);
        TaskStateEntity newTaskState = serviceHelper.getTaskStateOrThrowException(taskStateId);
        if (task.getTaskState().equals(newTaskState)) {
            throw new BadRequestException("This task already belongs to the specified task state.");
        }
        task.setTaskState(newTaskState);
        task = taskRepository.saveAndFlush(task);
        return taskDtoFactory.makeTaskDto(task);
    }

    public AckDto deleteTask(Long taskId){
        TaskEntity changeTask = serviceHelper.getTaskOrThrowException(taskId);
        taskRepository.delete(changeTask);
        return AckDto.builder().answer(true).build();
    }

}
