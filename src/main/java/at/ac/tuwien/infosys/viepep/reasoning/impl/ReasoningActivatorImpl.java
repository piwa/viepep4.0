package at.ac.tuwien.infosys.viepep.reasoning.impl;

import at.ac.tuwien.infosys.viepep.database.inmemory.database.InMemoryCacheImpl;
import at.ac.tuwien.infosys.viepep.database.inmemory.services.CacheDockerService;
import at.ac.tuwien.infosys.viepep.database.inmemory.services.CacheVirtualMachineService;
import at.ac.tuwien.infosys.viepep.reasoning.ReasoningActivator;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.Future;

/**
 * Created by philippwaibel on 17/05/16. edited by Gerta Sheganaku
 */
@Component
@Scope("prototype")
@Slf4j
public class ReasoningActivatorImpl implements ReasoningActivator {

    @Autowired
    private ReasoningImpl reasoning;
    @Autowired
    private CacheVirtualMachineService cacheVirtualMachineService;
    @Autowired
    private CacheDockerService cacheDockerService;
    @Autowired
    private InMemoryCacheImpl inMemoryCache;
    
    @Value("${reasoner.autoTerminate}")
    private boolean autoTerminate;

    @Value("${use.docker}")
    private boolean useDocker;

    @Override
    public void initialize() {
        log.info("ReasoningActivator initialized");

        inMemoryCache.clear();

        if(useDocker) {
            cacheDockerService.initializeDockerContainers();
        }
        
        cacheVirtualMachineService.initializeVMs();
        
    }

    @Override
    public Future<Boolean> start() throws Exception {
    	return reasoning.runReasoning(new Date(), autoTerminate);
    }

    @Override
    public void stop() {
        reasoning.stop();
    }
}
