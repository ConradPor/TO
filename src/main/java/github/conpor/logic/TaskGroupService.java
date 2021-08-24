package github.conpor.logic;


import github.conpor.model.Project;
import github.conpor.model.TaskGroup;
import github.conpor.model.TaskGroupRepository;
import github.conpor.model.TaskRepository;
import github.conpor.model.projection.GroupReadModel;
import github.conpor.model.projection.GroupWriteModel;

import java.util.List;
import java.util.stream.Collectors;


class TaskGroupService {

    private TaskGroupRepository repository;
    private TaskRepository taskRepository;


    TaskGroupService(final TaskGroupRepository repository, final TaskRepository taskRepository) {
        this.repository = repository;
        this.taskRepository = taskRepository;

    }

    public GroupReadModel createGroup(final GroupWriteModel source) {
        return createGroup(source, null);
    }

    GroupReadModel createGroup(final GroupWriteModel source, final Project project) {
        TaskGroup result = repository.save(source.toGroup(project));
        return new GroupReadModel(result);
    }

    public List<GroupReadModel> readAll() {
        return repository.findAll().stream()
                .map(GroupReadModel::new)
                .collect(Collectors.toList());
    }


    public void toggleGroup(int groupId){
        if (taskRepository.existsByDoneIsFalseAndGroup_Id(groupId)) {
            throw new IllegalStateException("Group has undone tasks. Done all the tasks first");
        }
        TaskGroup result = repository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("TaskGroup with given id not found"));
        result.setDone(!result.isDone());
        repository.save(result);

    }
}

    /*@Autowired
    List<String> temp(TaskGroupRepository repository) {
        return repository.findAll().stream()
                .flatMap(taskGroup ->  taskGroup.getTasks().stream())
                .map(Task::getDescription)
                .collect(Collectors.toList());
        //problem przy iterowaniu po getTask grupy podczas lazyloading

    }*/

