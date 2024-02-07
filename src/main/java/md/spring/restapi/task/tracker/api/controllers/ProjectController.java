package md.spring.restapi.task.tracker.api.controllers;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import md.spring.restapi.task.tracker.api.dto.AckDto;
import md.spring.restapi.task.tracker.api.dto.ProjectDto;
import md.spring.restapi.task.tracker.api.services.ProjectService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
@RestController
public class ProjectController {

   ProjectService projectService;
   public static final String FETCH_PROJECTS = "/api/projects";
   public static final String CREATE_PROJECT = "/api/projects";
   public static final String EDIT_PROJECT = "/api/projects/{project_id}";
   public static final String DELETE_PROJECT = "/api/projects/{project_id}";


   @GetMapping(FETCH_PROJECTS)
   public List<ProjectDto> fetchProjects(
           @RequestParam(value = "prefix_name", required = false)Optional<String> optionalPrefixName){
      return projectService.fetchProjects(optionalPrefixName);
   }

   @PutMapping(CREATE_PROJECT)
   public ProjectDto createProject(@RequestParam String projectName){
      return projectService.createProject(projectName);
   }

   @PatchMapping(EDIT_PROJECT)
   public ProjectDto updateProject(@RequestParam String projectName,
                                   @PathVariable("project_id") Long projectId){
      return projectService.editProject(projectName,projectId);
   }

   @DeleteMapping(DELETE_PROJECT)
   public AckDto deleteProject(@PathVariable("project_id") Long projectId){
      return projectService.deleteProject(projectId);
   }





}
