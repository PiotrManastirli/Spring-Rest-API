package md.spring.restapi.task.tracker.api.controllers;

import md.spring.restapi.task.tracker.api.dto.TaskStateDto;
import md.spring.restapi.task.tracker.api.services.TaskStateService;
import md.spring.restapi.task.tracker.store.entities.TaskStateEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class TaskStateControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskStateService taskStateService;

    @Test
    @WithMockUser(authorities="USER")
    void getTaskStates() throws Exception {
        Long projectId = 1L;
        TaskStateDto taskStateDto = TaskStateDto.builder()
                .id(1L)
                .name("Task State 1")
                .createdAt(Instant.now())
                .build();
        List<TaskStateDto> taskStateDtos = Collections.singletonList(taskStateDto);

        when(taskStateService.getTaskStates(anyLong())).thenReturn(taskStateDtos);

        mockMvc.perform(get("/api/projects/{project_id}/tasks-states", projectId))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Task State 1"));
    }


    @Test
    @WithMockUser(authorities="USER")
    void createTaskState() throws Exception {
        Long projectId = 1L;
        String taskStateName = "New task state";
        TaskStateDto taskStateDto = TaskStateDto.builder()
                .id(1L)
                .name(taskStateName)
                .createdAt(Instant.now())
                .build();
        when(taskStateService.createTaskState(anyLong(),anyString())).thenReturn(taskStateDto);
        mockMvc.perform(post("/api/projects/{project_id}/tasks-states", projectId)
                        .param("task_state_name", taskStateName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value(taskStateName));
    }

    @Test
    @WithMockUser(authorities="USER")
    void updateTaskState() throws Exception {
        Long taskStateId = 1L;
        String taskStateName = "New Task State Name";
        TaskStateEntity taskStateEntity = new TaskStateEntity();
        taskStateEntity.setId(taskStateId);
        taskStateEntity.setName("Old Task State Name");
        TaskStateDto updatedTaskStateDto = TaskStateDto.builder()
                .id(taskStateId)
                .name(taskStateName)
                .createdAt(Instant.now())
                .build();
        when(taskStateService.updateTaskState(anyLong(), anyString())).thenReturn(updatedTaskStateDto);

        mockMvc.perform(patch("/api/tasks-states/{task_state_id}", taskStateId)
                        .param("task_state_name", taskStateName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskStateId))
                .andExpect(jsonPath("$.name").value(taskStateName));
    }
    @Test
    @WithMockUser(authorities="USER")
    public void testDeleteTaskState() throws Exception {
        Long taskStateId = 1L;
        mockMvc.perform(delete("/api/task-states/" + taskStateId))
                .andExpect(status().isOk());
    }
}