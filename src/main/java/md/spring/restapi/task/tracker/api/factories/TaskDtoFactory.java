package md.spring.restapi.task.tracker.api.factories;

import md.spring.restapi.task.tracker.api.dto.TaskDto;
import md.spring.restapi.task.tracker.store.entities.TaskEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;


@Component
public class TaskDtoFactory {

    public TaskDto makeTaskDto(TaskEntity entity){
        return TaskDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .description(entity.getDescription())
                .taskState(entity
                        .getTaskState().getName()
                )
                .build();
    }
}
