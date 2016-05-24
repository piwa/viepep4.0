package at.ac.tuwien.infosys.viepep.database.repositories;

import at.ac.tuwien.infosys.viepep.database.entities.Element;
import at.ac.tuwien.infosys.viepep.database.entities.VirtualMachine;
import at.ac.tuwien.infosys.viepep.database.entities.WorkflowElement;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by philippwaibel on 17/05/16.
 */

public interface WorkflowElementRepository extends CrudRepository<WorkflowElement, Long> {

    @Cacheable(value = "WorkflowElementCache")
    @Query("select p.name from WorkflowElement p")
    List<String> getWorkflowInstanceIds();

    @Cacheable(value = "WorkflowElementCache")
    @Query("select distinct w from WorkflowElement w where (select count(s.name) from ProcessStep s where s.finishedAt is NULL and s.hasToBeExecuted = true and s.workflowName=w.name)>0")
    List<WorkflowElement> getList() ;

    @Cacheable(value = "WorkflowElementCache")
    Iterable<WorkflowElement> findAll();

    @CacheEvict(cacheNames = {"ElementCache", "WorkflowElementCache", "ProcessStepElementCache"}, allEntries = true)
    <S extends WorkflowElement> S save(S entity);
}
