package md.spring.restapi.task.tracker.api.factories;

import lombok.RequiredArgsConstructor;
import md.spring.restapi.task.tracker.store.entities.TaskEntity;
import md.spring.restapi.task.tracker.api.dto.TaskStateDto;
import md.spring.restapi.task.tracker.store.entities.TaskStateEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class TaskStateDtoFactory {
    public TaskStateDto makeTaskStateDto(TaskStateEntity entity){
        return TaskStateDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .leftTaskStateId(entity.getLeftTaskState().map(TaskStateEntity::getId).orElse(null))
                .rightTaskStateId(entity.getRightTaskState().map(TaskStateEntity::getId).orElse(null))
                .taskIds(entity
                                .getTasks()
                                .stream()
                                .map(TaskEntity::getId)
                                .collect(Collectors.toList())
                )
                .build();
    }
}
