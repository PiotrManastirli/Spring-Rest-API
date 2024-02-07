package md.spring.restapi.task.tracker.api.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import md.spring.restapi.task.tracker.api.dto.AckDto;
import md.spring.restapi.task.tracker.api.dto.ProjectDto;
import md.spring.restapi.task.tracker.api.exceptions.BadRequestException;
import md.spring.restapi.task.tracker.api.factories.ProjectDtoFactory;
import md.spring.restapi.task.tracker.api.services.helpers.ServiceHelper;
import md.spring.restapi.task.tracker.store.entities.ProjectEntity;
import md.spring.restapi.task.tracker.store.repositories.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class ProjectService {
    ProjectRepository projectRepository;

    ProjectDtoFactory projectDtoFactory;

    ServiceHelper serviceHelper;

    public List<ProjectDto> fetchProjects(Optional<String> optionalPrefixName) {
        optionalPrefixName = optionalPrefixName
                .filter(prefixName -> !prefixName.trim().isEmpty());
        Stream<ProjectEntity> projectStream = optionalPrefixName
                .map(projectRepository::streamAllByNameStartsWithIgnoreCase)
                .orElseGet(projectRepository::streamAllBy);
        return projectStream
                .map(projectDtoFactory::makeProjectDto)
                .collect(Collectors.toList());
    }

    public ProjectDto createProject(String name){
        if(name.trim().isEmpty()){
            throw new BadRequestException("Name can't be empty");
        }
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

    public ProjectDto editProject(String name, Long projectId){

        if(name.trim().isEmpty()){
            throw new BadRequestException("Name can't be empty");
        }

        ProjectEntity project = serviceHelper.getProjectOrThrowException(projectId);
        projectRepository
                .findByName(name)
                .filter(anotherProject -> !Objects.equals(anotherProject.getId(),projectId))
                .ifPresent(anotherProject -> {
                    throw new BadRequestException(String.format("Project \"%s\" already exists.", name));
                });
        project.setName(name);
        project = projectRepository.saveAndFlush(project);
        return  projectDtoFactory.makeProjectDto(project);
    }
    public AckDto deleteProject(Long projectId){
        serviceHelper.getProjectOrThrowException(projectId);
        projectRepository.deleteById(projectId);
        return AckDto.makeDefault(true);
    }
}
