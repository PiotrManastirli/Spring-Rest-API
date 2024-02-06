package md.spring.restapi.task.tracker.api.factories;

import md.spring.restapi.task.tracker.api.dto.TaskStateDto;
import md.spring.restapi.task.tracker.store.entities.TaskStateEntity;
import org.springframework.stereotype.Component;


@Component
public class TaskStateDtoFactory {

    public TaskStateDto makeTaskStateDto(TaskStateEntity entity){
        return TaskStateDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .ordinal(entity.getOrdinal())
                .build();
    }
}
