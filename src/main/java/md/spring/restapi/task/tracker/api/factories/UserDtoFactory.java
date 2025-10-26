package md.spring.restapi.task.tracker.api.factories;

import md.spring.restapi.task.tracker.api.dto.UserDto;
import md.spring.restapi.task.tracker.store.entities.TaskEntity;
import md.spring.restapi.task.tracker.store.entities.UserEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserDtoFactory {
    public UserDto makeUserDto(UserEntity entity){
        return UserDto.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .password(entity.getPassword())
                .role(entity.getRole())
                .taskIds(entity
                        .getTasks()
                        .stream()
                        .map(TaskEntity::getId)
                        .collect(Collectors.toList())
                )
                .build();
    }
}
