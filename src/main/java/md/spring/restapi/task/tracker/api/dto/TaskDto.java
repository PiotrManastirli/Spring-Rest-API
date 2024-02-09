package md.spring.restapi.task.tracker.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import md.spring.restapi.task.tracker.store.entities.TaskStateEntity;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaskDto {
    Long id;

    String name;
    @JsonProperty("created_at")
    Instant createdAt;

    String description;

}
