package md.spring.restapi.task.tracker.api.controllers;

import md.spring.restapi.task.tracker.api.dto.AckDto;
import md.spring.restapi.task.tracker.api.dto.ProjectDto;
import md.spring.restapi.task.tracker.api.dto.TaskDto;
import md.spring.restapi.task.tracker.api.services.TaskService;
import md.spring.restapi.task.tracker.api.services.TaskStateService;
import md.spring.restapi.task.tracker.store.entities.TaskEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class TasksControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;


    @Test
    @WithMockUser(authorities="ADMIN")
    void getTasks() throws Exception {
        Long taskStateId = 1L;
        TaskDto taskDto = TaskDto.builder()
                .id(1L)
                .name("Task 1")
                .createdAt(Instant.now())
                .build();
        List<TaskDto> taskDtos = Collections.singletonList(taskDto);
        when(taskService.getTasks(any())).thenReturn(taskDtos);
        mockMvc.perform(get("/api/projects/{task_state_id}/tasks",taskStateId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Task 1"));
    }

    @Test
    @WithMockUser(authorities="ADMIN")
    void getUserTasks() throws Exception {
        Long userId = 1L;
        TaskDto taskDto = TaskDto.builder()
                .id(1L)
                .name("Task 1")
                .createdAt(Instant.now())
                .build();
        List<TaskDto> taskDtos = Collections.singletonList(taskDto);
        when(taskService.getUserTasks(any())).thenReturn(taskDtos);
        mockMvc.perform(get("/api/projects/user/{user_id}/tasks",userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Task 1"));
    }



    @Test
    @WithMockUser(authorities="ADMIN")
    void createTask() throws Exception {
        Long taskStateId = 1L;
        String taskName = "Task 1";
        String taskDescription = "Task description";
        Long userId = 1L;
        TaskDto taskDto = TaskDto.builder()
                .id(1L)
                .name(taskName)
                .createdAt(Instant.now())
                .build();

        when(taskService.createTask(anyLong(), anyString(), anyString(), anyLong())).thenReturn(taskDto);

        mockMvc.perform(post("/api/projects/{task_state_id}/task", taskStateId)
                        .param("task_name", taskName)
                        .param("task_description", taskDescription)
                        .param("user_id", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Task 1"));
    }


    @Test
    @WithMockUser(authorities="ADMIN")
    void updateTask() throws Exception {
        Long taskId = 1L;
        String taskName = "Updated Task Name";
        String taskDescription = "Updated Task Description";

        TaskDto updatedTaskDto = TaskDto.builder()
                .id(taskId)
                .name(taskName)
                .createdAt(Instant.now())
                .description(taskDescription)
                .build();

        when(taskService.updateTask(anyLong(), any(), any())).thenReturn(updatedTaskDto);

        mockMvc.perform(patch("/api/tasks/{task_id}", taskId)
                        .param("task_name", taskName)
                        .param("task_description", taskDescription))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId))
                .andExpect(jsonPath("$.name").value(taskName))
                .andExpect(jsonPath("$.description").value(taskDescription));
    }

    @Test
    @WithMockUser(authorities="ADMIN")
    void changeTaskPosition() throws Exception {
        Long taskId = 1L;
        Long taskStateId = 2L;
        TaskDto updatedTaskDto = TaskDto.builder()
                .id(taskId)
                .name("Task Name")
                .createdAt(Instant.now())
                .build();
        when(taskService.changeTaskPosition(anyLong(), anyLong())).thenReturn(updatedTaskDto);
        mockMvc.perform(patch("/api/tasks/{task_id}/position/change", taskId)
                        .param("task_state_id", String.valueOf(taskStateId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId))
                .andExpect(jsonPath("$.name").value("Task Name"));
    }

    @Test
    @WithMockUser(authorities="ADMIN")
    void deleteTask() throws Exception {
        Long taskId = 1L;
        AckDto ackDto = AckDto.builder().answer(true).build();
        when(taskService.deleteTask(anyLong())).thenReturn(ackDto);
        mockMvc.perform(delete("/api/tasks/delete/{task_id}", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").value(true));
    }
}