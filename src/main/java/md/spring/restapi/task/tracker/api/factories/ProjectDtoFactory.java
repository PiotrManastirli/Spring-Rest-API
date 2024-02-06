package md.spring.restapi.task.tracker.api.factories;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import md.spring.restapi.task.tracker.api.dto.ProjectDto;
import md.spring.restapi.task.tracker.store.entities.ProjectEntity;
import org.springframework.stereotype.Component;


@Component
public class ProjectDtoFactory {

    public ProjectDto makeProjectDto(ProjectEntity entity){
        return ProjectDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
