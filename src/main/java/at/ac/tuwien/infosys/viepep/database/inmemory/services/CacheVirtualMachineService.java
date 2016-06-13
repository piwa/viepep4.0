package at.ac.tuwien.infosys.viepep.database.inmemory.services;

import at.ac.tuwien.infosys.viepep.database.entities.VMType;
import at.ac.tuwien.infosys.viepep.database.entities.VirtualMachine;
import at.ac.tuwien.infosys.viepep.database.inmemory.database.InMemoryCacheImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by philippwaibel on 10/06/16.
 */
@Component
@Slf4j
public class CacheVirtualMachineService {

    @Autowired
    private InMemoryCacheImpl inMemoryCache;

    private int V = 6; //Available types of VM's
    private int K = 2;

    public List<VirtualMachine> getVMs() {
        return inMemoryCache.getVMs();
    }

    public void addVM(VirtualMachine vm) {
        inMemoryCache.addVM(vm);
    }

    public void initializeVMs() {
        List<VirtualMachine> vms = new ArrayList<>();
        try {
            for (int v = 0; v < V; v++) {
                VMType type = VMType.fromIdentifier(v + 1);
                for (int k = 0; k < K; k++) {
                    vms.add(new VirtualMachine(v + "_" + k, type));
                }
            }
        } catch (Exception ex) {
            log.error("EXCEPTION", ex);
        }

        for(VirtualMachine vm : vms) {
            addVM(vm);
        }
    }
}
