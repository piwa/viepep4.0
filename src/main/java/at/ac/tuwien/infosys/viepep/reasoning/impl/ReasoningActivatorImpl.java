package at.ac.tuwien.infosys.viepep.reasoning.impl;

import at.ac.tuwien.infosys.viepep.database.inmemory.database.InMemoryCacheImpl;
import at.ac.tuwien.infosys.viepep.database.inmemory.services.CacheDockerService;
import at.ac.tuwien.infosys.viepep.database.inmemory.services.CacheVirtualMachineService;
import at.ac.tuwien.infosys.viepep.reasoning.Reasoning;
import at.ac.tuwien.infosys.viepep.reasoning.ReasoningActivator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.Future;

/**
 * Created by philippwaibel on 17/05/16.
 */
@Component
@Scope("prototype")
@Slf4j
public class ReasoningActivatorImpl implements ReasoningActivator {

    @Autowired
    private Reasoning reasoningVmImpl;
    @Autowired
    private Reasoning reasoningDockerImpl;
    @Autowired
    private CacheVirtualMachineService cacheVirtualMachineService;
    @Autowired
    private CacheDockerService cacheDockerService;
    @Autowired
    private InMemoryCacheImpl inMemoryCache;

    @Value("${use.docker}")
    private boolean useDocker;

    @Override
    public void initialize() {
        log.info("ReasoningActivator initialized");

        inMemoryCache.clear();

        if(useDocker) {
            cacheDockerService.initializeDockerImages();
        }
        else {
            cacheVirtualMachineService.initializeVMs();
        }
    }

    @Override
    public Future<Boolean> start() throws Exception {

        if(useDocker) {
            return reasoningDockerImpl.runReasoning(new Date());
        }
        else {
            return reasoningVmImpl.runReasoning(new Date());
        }

    }

    @Override
    public void stop() {
        if(useDocker) {
            reasoningDockerImpl.stop();
        }
        else {
            reasoningVmImpl.stop();
        }
    }
}
