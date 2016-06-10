package at.ac.tuwien.infosys.viepep.database.inmemory.services;

import at.ac.tuwien.infosys.viepep.database.entities.VirtualMachine;
import at.ac.tuwien.infosys.viepep.database.inmemory.database.InMemoryCacheImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by philippwaibel on 10/06/16.
 */
@Component
public class CacheVirtualMachineService {

    @Autowired
    private InMemoryCacheImpl inMemoryCache;

    public List<VirtualMachine> getVMs() {
        return inMemoryCache.getVMs();
    }

    public void addVM(VirtualMachine vm) {
        inMemoryCache.addVM(vm);
    }

}
