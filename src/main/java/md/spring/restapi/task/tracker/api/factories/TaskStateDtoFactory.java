package md.spring.restapi.task.tracker.api.factories;

import lombok.RequiredArgsConstructor;
import md.spring.restapi.task.tracker.api.dto.TaskDto;
import md.spring.restapi.task.tracker.api.dto.TaskStateDto;
import md.spring.restapi.task.tracker.store.entities.TaskStateEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class TaskStateDtoFactory {

    private final TaskDtoFactory taskDtoFactory;
    public TaskStateDto makeTaskStateDto(TaskStateEntity entity){
        return TaskStateDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .leftTaskStateId(entity.getLeftTaskState().map(TaskStateEntity::getId).orElse(null))
                .rightTaskStateId(entity.getRightTaskState().map(TaskStateEntity::getId).orElse(null))
                .tasks(
                        entity
                        .getTasks()
                        .stream()
                        .map(taskDtoFactory::makeTaskDto)
                        .collect(Collectors.toList())
                )
                .build();
    }
}
