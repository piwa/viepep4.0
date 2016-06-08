package at.ac.tuwien.infosys.viepep.database.repositories;

import at.ac.tuwien.infosys.viepep.database.entities.WorkflowElement;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by philippwaibel on 17/05/16.
 */

public interface WorkflowElementRepository extends CrudRepository<WorkflowElement, Long> {

    @Cacheable(value = "WorkflowElementCache")
    Iterable<WorkflowElement> findAll();

    @CacheEvict(cacheNames = {"ElementCache", "WorkflowElementCache", "ProcessStepElementCache"}, allEntries = true)
    <S extends WorkflowElement> S save(S entity);
}
