package at.ac.tuwien.infosys.viepep.database.repositories;

import at.ac.tuwien.infosys.viepep.database.entities.VirtualMachine;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by philippwaibel on 17/05/16.
 */
public interface VirtualMachineRepository extends CrudRepository<VirtualMachine, Long> {

    @Cacheable(cacheNames = "ElementCache")
    Iterable<VirtualMachine> findAll();

    @CacheEvict(cacheNames = "ElementCache", allEntries = true)
    <S extends VirtualMachine> S save(S entity);

    @CacheEvict(cacheNames = "ElementCache", allEntries = true)
    <S extends VirtualMachine> Iterable<S> save(Iterable<S> entities);
}
