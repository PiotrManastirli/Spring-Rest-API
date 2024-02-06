package md.spring.restapi.task.tracker.api.controllers;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import md.spring.restapi.task.tracker.api.dto.ProjectDto;
import md.spring.restapi.task.tracker.api.services.ProjectService;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
@RestController
public class ProjectController {

   ProjectService projectService;
   public static final String CREATE_PROJECT = "/api/projects";
   public static final String EDITE_PROJECT = "/api/projects/{project_id}";

   @PostMapping(CREATE_PROJECT)
    public ProjectDto createProject(@RequestParam String name){
         return projectService.createProject(name);
   }

   @PatchMapping(EDITE_PROJECT)
   public ProjectDto updateProject(@RequestParam String name,
                                   @PathVariable("project_id") Long projectId){
      return projectService.editeProject(name,projectId);
   }





}
