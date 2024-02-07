package md.spring.restapi.task.tracker.api.controllers;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import md.spring.restapi.task.tracker.api.dto.AckDto;
import md.spring.restapi.task.tracker.api.dto.ProjectDto;
import md.spring.restapi.task.tracker.api.dto.TaskStateDto;
import md.spring.restapi.task.tracker.api.services.TaskStateService;
import md.spring.restapi.task.tracker.store.entities.ProjectEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
@RestController
public class TaskStateController {
    TaskStateService taskStateService;
    public static final String GET_TASK_STATE = "/api/projects/{project_id}/tasks-states";
    public static final String CREATE_TASK_STATE = "/api/projects/{project_id}/tasks-states";

    public static final String UPDATE_TASK_STATE = "/api/tasks-states/{task_state_id}";
    public static final String EDIT_PROJECT = "/api/projects/{project_id}";
    public static final String DELETE_PROJECT = "/api/projects/{project_id}";

    @GetMapping(GET_TASK_STATE)
    public List<TaskStateDto> getTaskStates(@PathVariable(name = "project_id") Long projectId){
        return taskStateService.getTaskStates(projectId);
    }

    @PostMapping(CREATE_TASK_STATE)
    public TaskStateDto createTaskState(
            @PathVariable(name = "project_id") Long projectId,
            @RequestParam(name = "task_state_name") String taskStateName){
        return taskStateService.createTaskState(projectId,taskStateName);
    }

    @PostMapping(UPDATE_TASK_STATE)
    public TaskStateDto updateTaskState(
            @PathVariable(name = "task_state_id") Long taskStateId,
            @RequestParam(name = "task_state_name") String taskStateName){
        return taskStateService.updateTaskState(taskStateId,taskStateName);
    }


}
