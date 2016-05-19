package at.ac.tuwien.infosys.viepep.database.repositories;

import at.ac.tuwien.infosys.viepep.database.entities.Element;
import at.ac.tuwien.infosys.viepep.database.entities.WorkflowElement;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by philippwaibel on 17/05/16.
 */

public interface WorkflowElementRepository extends CrudRepository<WorkflowElement, Long> {

    @Query("select p.name from WorkflowElement p")
    List<String> getWorkflowInstanceIds();

    @Query("select distinct w from WorkflowElement w where (select count(s.name) from ProcessStep s where s.finishedAt is NULL and s.hasToBeExecuted = true and s.workflowName=w.name)>0")
    List<WorkflowElement> getList() ;

    @Query("select p from ProcessStep p where p.finishedAt is null and p.startDate is null ")
    List<Element> getUnfinishedSteps();
}
