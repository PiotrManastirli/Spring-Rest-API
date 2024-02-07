package md.spring.restapi.task.tracker.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.OneToOne;
import lombok.*;
import lombok.experimental.FieldDefaults;
import md.spring.restapi.task.tracker.store.entities.TaskStateEntity;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaskStateDto {
    @NonNull
    Long id;
    @JsonProperty("left_task_state_id")
    Long leftTaskStateId;
    @JsonProperty("right_task_state_id")
    Long rightTaskStateId;
    @NonNull
    String name;
    @NonNull
    @JsonProperty("created_at")
    Instant createdAt;

    @NonNull
    List<TaskDto> tasks;
}
