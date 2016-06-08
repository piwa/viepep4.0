package at.ac.tuwien.infosys.viepep.database.repositories;

import at.ac.tuwien.infosys.viepep.database.entities.ProcessStep;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by philippwaibel on 17/05/16.
 */
public interface ProcessStepElementRepository extends CrudRepository<ProcessStep, Long> {

    @Cacheable(value = "ProcessStepElementCache")
    @Query("select p from ProcessStep p where p.finishedAt is null and p.startDate is null")
    List<ProcessStep> getUnfinishedSteps();

    @Cacheable(value = "ProcessStepElementCache")
    @Query("select p from ProcessStep p where p.scheduledAtVM.id = ?1")
    List<ProcessStep> findByVM(Long virtualMachineId) ;

    @CacheEvict(cacheNames = {"ElementCache", "WorkflowElementCache", "ProcessStepElementCache"}, allEntries = true)
    <S extends ProcessStep> S save(S entity);
}
