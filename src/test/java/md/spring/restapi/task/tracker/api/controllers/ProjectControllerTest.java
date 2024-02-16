package md.spring.restapi.task.tracker.api.controllers;

import md.spring.restapi.task.tracker.api.dto.AckDto;
import md.spring.restapi.task.tracker.api.dto.ProjectDto;
import md.spring.restapi.task.tracker.api.exceptions.BadRequestException;
import md.spring.restapi.task.tracker.api.exceptions.NotFoundException;
import md.spring.restapi.task.tracker.api.services.ProjectService;
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
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    @Test
    @WithMockUser(authorities="USER")
    void fetchProjects() throws Exception {
        ProjectDto projectDto = ProjectDto.builder()
                .id(1L)
                .name("Project 1")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        List<ProjectDto> projectDtos = Collections.singletonList(projectDto);
        when(projectService.fetchProjects(any())).thenReturn(projectDtos);

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Project 1"));
    }

    @Test
    @WithMockUser(authorities="USER")
    void createProject() throws Exception {
        String projectName = "Project 1";

        ProjectDto projectDto = ProjectDto.builder()
                .id(1L)
                .name(projectName)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        when(projectService.createProject(projectName)).thenReturn(projectDto);

        mockMvc.perform(put("/api/projects/create")
                        .param("projectName", projectName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value(projectName));
    }
    @Test
    @WithMockUser(authorities="USER")
    void updateProject_Success() throws Exception {
        String projectName = "Updated Project";
        Long projectId = 1L;
        ProjectDto projectDto = ProjectDto.builder()
                .id(projectId)
                .name(projectName)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        projectDto.setName("new Project");

        when(projectService.editProject(anyString(), anyLong())).thenReturn(projectDto);

        mockMvc.perform(patch("/api/projects/{project_id}", projectId)
                        .param("projectName", projectName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(projectId))
                .andExpect(jsonPath("$.name").value("new Project"));
    }

    @Test
    @WithMockUser(authorities="USER")
    void updateProject_Failure() throws Exception {
        String projectName = "Existing Project";
        Long projectId = 1L;

        when(projectService.editProject(anyString(), anyLong()))
                .thenThrow(new BadRequestException(String.format("Project \"%s\" already exists.", projectName)));

        mockMvc.perform(patch("/api/projects/{project_id}", projectId)
                        .param("name", projectName))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities="USER")
    void deleteProject_Success() throws Exception {
        Long projectId = 1L;
        AckDto ackDto = AckDto.makeDefault(true);

        when(projectService.deleteProject(anyLong())).thenReturn(ackDto);

        mockMvc.perform(delete("/api/projects/{project_id}", projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").value(true));
    }

    @Test
    @WithMockUser(authorities="USER")
    void deleteProject_Failure() throws Exception {
        Long projectId = 1L;

        when(projectService.deleteProject(anyLong()))
                .thenThrow(new NotFoundException(String.format("Project with id \"%s\" not found.", projectId)));

        mockMvc.perform(delete("/api/projects/{project_id}", projectId))
                .andExpect(status().isNotFound());
    }
}