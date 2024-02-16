package md.spring.restapi.task.tracker.api.controllers;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import md.spring.restapi.task.tracker.api.dto.AckDto;
import md.spring.restapi.task.tracker.api.dto.TaskDto;
import md.spring.restapi.task.tracker.api.dto.TaskStateDto;
import md.spring.restapi.task.tracker.api.services.TaskService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
@RestController
public class TasksController {

    TaskService taskService;

    public static final String GET_TASKS = "/api/projects/{task_state_id}/tasks";
    public static final String GET_USER_TASKS = "/api/projects/user/{user_id}/tasks";
    public static final String GET_PROJECT_TASKS = "/api/projects/project/{project_id}/tasks";
    public static final String CREATE_TASK = "/api/projects/{task_state_id}/task";
    public static final String UPDATE_TASK= "/api/tasks/{task_id}";
    public static final String CHANGE_TASK_POSITION = "/api/tasks/{task_id}/position/change";
    public static final String DELETE_TASK = "/api/tasks/delete/{task_id}";

    @GetMapping(GET_TASKS)
    public List<TaskDto> getTasks(@PathVariable(name = "task_state_id") Long taskStateId){
        return taskService.getTasks(taskStateId);
    }
    @GetMapping(GET_USER_TASKS)
    public List<TaskDto> getUserTasks(@PathVariable(name = "user_id") Long userId) {
        return taskService.getUserTasks(userId);
    }

    @GetMapping(GET_PROJECT_TASKS)
    public List<TaskDto> getProjectTasks(@PathVariable(name = "project_id") Long projectId) {
        return taskService.getTasksByProject(projectId);
    }

    @PostMapping(CREATE_TASK)
    public TaskDto createTask(
            @PathVariable(name = "task_state_id") Long taskStateId,
            @RequestParam(name = "task_name") String taskStateName,
            @RequestParam(name = "task_description") String taskDescription,
            @RequestParam(name = "user_id") Long userId){
        return taskService.createTask(taskStateId,taskStateName,taskDescription,userId);
    }

    @PatchMapping(UPDATE_TASK)
    public TaskDto updateTask(
            @PathVariable(name = "task_id") Long taskId,
            @RequestParam(name = "task_name",required = false) Optional<String> taskName,
            @RequestParam(name = "task_description",required = false) Optional<String> taskDescription){
        return taskService.updateTask(taskId,taskName,taskDescription);
    }

    @PatchMapping(CHANGE_TASK_POSITION)
    public TaskDto changeTaskPosition(
            @PathVariable(name = "task_id") Long taskId,
            @RequestParam(name = "task_state_id") Long taskStateId){
        return taskService.changeTaskPosition(taskId,taskStateId);
    }

    @DeleteMapping(DELETE_TASK)
    public AckDto deleteTaskState(@PathVariable(name = "task_id") Long taskId){
        return taskService.deleteTask(taskId);
    }

}
