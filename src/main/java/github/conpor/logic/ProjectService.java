package github.conpor.logic;

import github.conpor.TaskConfigurationProperties;
import github.conpor.model.*;
import github.conpor.model.projection.GroupReadModel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


public class ProjectService {

    private TaskGroupRepository taskGroupRepository;
    private ProjectRepository repository;
    private TaskConfigurationProperties config;

    public ProjectService (final ProjectRepository repository,final TaskGroupRepository taskGroupRepository, final TaskConfigurationProperties config) {
        this.repository = repository;
        this.taskGroupRepository = taskGroupRepository;
        this.config = config;
    }


    public List<Project> readAll() {
        return repository.findAll();
    }

    public Project save(final Project toSave) {
        return repository.save(toSave);
    }

    public GroupReadModel createGroup(LocalDateTime deadline, int projectId) {
        if (!config.getTemplate().isAllowMultipleTasks() && taskGroupRepository.existsByDoneIsFalseAndProject_Id(projectId)) {
            throw new IllegalStateException("Only one undone  group from project is allowed!");
        }
        TaskGroup result = repository.findById(projectId)
                .map(project -> {
                    var rst = new TaskGroup();
                    rst.setDescription(project.getDescription());
                    rst.setTasks(
                            project.getSteps().stream()
                                    .map(projectStep -> new Task(
                                            projectStep.getDescription(),
                                            deadline.plusDays(projectStep.getDaysToDeadline()))
                                    ).collect(Collectors.toSet())
                    );
                    rst.setProject(project);
                    return taskGroupRepository.save(rst);
                }).orElseThrow(() -> new IllegalArgumentException("Project with given id not found"));
        return new GroupReadModel(result);

    }
}