package md.spring.restapi.task.tracker.api.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import md.spring.restapi.task.tracker.api.dto.ProjectDto;
import md.spring.restapi.task.tracker.api.exceptions.BadRequestException;
import md.spring.restapi.task.tracker.api.exceptions.NotFoundException;
import md.spring.restapi.task.tracker.api.factories.ProjectDtoFactory;
import md.spring.restapi.task.tracker.store.entities.ProjectEntity;
import md.spring.restapi.task.tracker.store.repositories.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class ProjectService {
    ProjectRepository projectRepository;

    ProjectDtoFactory projectDtoFactory;

    public ProjectDto createProject(String name){
        projectRepository
                .findByName(name)
                .ifPresent(project -> {
                    throw new BadRequestException(String.format("Project \"%s\" already exists.", name));
                });
        ProjectEntity project = projectRepository.saveAndFlush(
          ProjectEntity.builder()
                  .name(name)
                  .build()
        );
        return  projectDtoFactory.makeProjectDto(project);
    }

    public ProjectDto editeProject(String name, Long projectId){
        ProjectEntity project = projectRepository
                .findById(projectId)
                .orElseThrow(() -> new NotFoundException(String.format("Project with \"%s\" doesn't exists.",
                        projectId))
                );
        projectRepository
                .findByName(name)
                .filter(anotherProject -> !Objects.equals(anotherProject.getId(),project.getId()))
                .ifPresent(anotherProject -> {
                    throw new BadRequestException(String.format("Project \"%s\" already exists.", name));
                });
        project.setName(name);
        return  projectDtoFactory.makeProjectDto(project);
    }
}
