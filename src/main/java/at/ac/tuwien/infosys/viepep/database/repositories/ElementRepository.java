package at.ac.tuwien.infosys.viepep.database.repositories;

import at.ac.tuwien.infosys.viepep.database.entities.Element;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by philippwaibel on 17/05/16.
 */
public interface ElementRepository extends CrudRepository<Element, Long> {

    @CacheEvict(cacheNames = {"ElementCache", "WorkflowElementCache", "ProcessStepElementCache"}, allEntries = true)
    <S extends Element> S save(S entity);

}
