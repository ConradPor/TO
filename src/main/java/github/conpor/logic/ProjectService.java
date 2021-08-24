package github.conpor.logic;

import github.conpor.TaskConfigurationProperties;
import github.conpor.model.Project;
import github.conpor.model.ProjectRepository;
import github.conpor.model.TaskGroupRepository;
import github.conpor.model.projection.GroupReadModel;
import github.conpor.model.projection.GroupTaskWriteModel;
import github.conpor.model.projection.GroupWriteModel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


public class ProjectService {

    private TaskGroupRepository taskGroupRepository;
    private ProjectRepository repository;
    private TaskConfigurationProperties config;
    private TaskGroupService service;

    public ProjectService (final ProjectRepository repository,final TaskGroupRepository taskGroupRepository, final TaskConfigurationProperties config, final TaskGroupService service) {
        this.repository = repository;
        this.taskGroupRepository = taskGroupRepository;
        this.config = config;
        this.service = service;
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
        GroupReadModel result = repository.findById(projectId)
                .map(project -> {
                    var targetGroup = new GroupWriteModel();
                    targetGroup.setDescription(project.getDescription());
                    targetGroup.setTasks(
                            project.getSteps().stream()
                                    .map(projectStep -> {
                                                var task = new GroupTaskWriteModel();
                                                task.setDescription(projectStep.getDescription());
                                                task.setDeadline(deadline.plusDays(projectStep.getDaysToDeadline()));
                                                return task;
                                            }
                                    ).collect(Collectors.toSet())
                    );
                    return service.createGroup(targetGroup);
                    }).orElseThrow(() -> new IllegalArgumentException("Project with given id not found"));
        return result;

    }
}