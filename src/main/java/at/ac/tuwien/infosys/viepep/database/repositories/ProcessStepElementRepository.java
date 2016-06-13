package at.ac.tuwien.infosys.viepep.database.repositories;

import at.ac.tuwien.infosys.viepep.database.entities.ProcessStep;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by philippwaibel on 17/05/16.
 */
public interface ProcessStepElementRepository extends CrudRepository<ProcessStep, Long> {

    @Query("select p from ProcessStep p where p.finishedAt is null and p.startDate is null")
    List<ProcessStep> getUnfinishedSteps();

    @Query("select p from ProcessStep p where p.scheduledAtVM.id = ?1")
    List<ProcessStep> findByVM(Long virtualMachineId) ;

    <S extends ProcessStep> S save(S entity);
}
