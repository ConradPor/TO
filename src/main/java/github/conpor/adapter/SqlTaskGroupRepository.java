package github.conpor.adapter;

import github.conpor.model.TaskGroup;
import github.conpor.model.TaskGroupRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SqlTaskGroupRepository extends TaskGroupRepository, JpaRepository<TaskGroup, Integer> {

    @Override
    @Query("select distinct g from TaskGroup g join fetch g.tasks") //Zapytanie na encjach
    List<TaskGroup> findAll();
    @Override
    boolean existsByDoneIsFalseAndProject_Id(Integer projectId);
}
