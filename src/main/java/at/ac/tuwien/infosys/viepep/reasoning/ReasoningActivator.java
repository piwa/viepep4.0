package at.ac.tuwien.infosys.viepep.reasoning;

import at.ac.tuwien.infosys.viepep.database.entities.VMType;
import at.ac.tuwien.infosys.viepep.database.entities.VirtualMachine;
import at.ac.tuwien.infosys.viepep.database.entities.WorkflowElement;
import at.ac.tuwien.infosys.viepep.database.inmemory.database.InMemoryCacheImpl;
import at.ac.tuwien.infosys.viepep.database.inmemory.services.CacheVirtualMachineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by philippwaibel on 17/05/16.
 */
@Component
@Scope("prototype")
@Slf4j
public class ReasoningActivator {

    @Autowired
    private Reasoning reasoning;
    @Autowired
    private CacheVirtualMachineService cacheVirtualMachineService;
    @Autowired
    private InMemoryCacheImpl inMemoryCache;

    private List<WorkflowElement> workflows;

    private int V = 6; //Available types of VM's
    private int K = 2;


    public void initialize() throws Exception {
        log.info("ReasoningActivator initialized");

        workflows = new ArrayList<>();

        inMemoryCache.clear();

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
            cacheVirtualMachineService.addVM(vm);
        }

        reasoning.runReasoning(new Date());
    }

    public void stop() {
        reasoning.stop();
    }
}
