package at.ac.tuwien.infosys.viepep.reasoning.optimisation.docker;

import at.ac.tuwien.infosys.viepep.database.entities.Element;
import at.ac.tuwien.infosys.viepep.database.entities.ProcessStep;
import at.ac.tuwien.infosys.viepep.database.entities.WorkflowElement;
import at.ac.tuwien.infosys.viepep.database.entities.docker.DockerConfiguration;
import at.ac.tuwien.infosys.viepep.database.inmemory.services.CacheWorkflowService;
import at.ac.tuwien.infosys.viepep.reasoning.optimisation.general.PlacementHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by philippwaibel on 25/09/2016.
 */
@Component
public class SimpleDockerOptimization {

    @Autowired
    private PlacementHelper placementHelper;
    @Autowired
    private CacheWorkflowService cacheWorkflowService;

    private List<WorkflowElement> nextWorkflowInstances;
    private Map<String, List<Element>> nextSteps;

    private long tau_t_1 = 0L;

    public DockerOptimizationResult optimize(Date tau_t_0) {

        placementHelper.setFinishedWorkflows();

        nextWorkflowInstances = null;
        nextWorkflowInstances = getNextWorkflowInstances();

        nextSteps = new HashMap<>();

        for (Element workflow : nextWorkflowInstances) {
            nextSteps = getNextSteps(workflow, nextSteps);
        }

        DockerOptimizationResult result = new DockerOptimizationResult();

        for(Map.Entry<String, List<Element>> nextStepsEntry : nextSteps.entrySet()) {
            for(Element nextStep : nextStepsEntry.getValue()) {
                DockerConfiguration dockerConfiguration = findBestFittingDockerConfig((ProcessStep)nextStep);
                result.getProcessStepDockerConfigMap().put((ProcessStep) nextStep, dockerConfiguration);
            }
        }

        tau_t_1 = tau_t_0.getTime() + 60 * 1000;
        result.setTau_t_1(tau_t_1);
        return result;

    }

    private DockerConfiguration findBestFittingDockerConfig(ProcessStep nextProcessStep) {
        if(serviceTypeFitsDockerConfig(DockerConfiguration.MICRO_CORE, nextProcessStep)) {
            return DockerConfiguration.MICRO_CORE;
        }
        else if(serviceTypeFitsDockerConfig(DockerConfiguration.SINGLE_CORE, nextProcessStep)) {
            return DockerConfiguration.SINGLE_CORE;
        }
        else if(serviceTypeFitsDockerConfig(DockerConfiguration.DUAL_CORE, nextProcessStep)) {
            return DockerConfiguration.DUAL_CORE;
        }
        else if(serviceTypeFitsDockerConfig(DockerConfiguration.QUAD_CORE, nextProcessStep)) {
            return DockerConfiguration.QUAD_CORE;
        }
        else if(serviceTypeFitsDockerConfig(DockerConfiguration.HEXA_CORE, nextProcessStep)) {
            return DockerConfiguration.HEXA_CORE;
        }

        return null;
    }

    private boolean serviceTypeFitsDockerConfig(DockerConfiguration dockerConfiguration, ProcessStep processStep) {
        if(dockerConfiguration.cores * 100 >= processStep.getServiceType().getCpuLoad() && dockerConfiguration.ram >= processStep.getServiceType().getMemory()) {
            return true;
        }
        return false;
    }



    /**
     * @return a list of workflow instances
     */
    public List<WorkflowElement> getNextWorkflowInstances() {
        if (nextWorkflowInstances == null) {
            nextWorkflowInstances = Collections.synchronizedList(new ArrayList<WorkflowElement>(cacheWorkflowService.getRunningWorkflowInstances()));
        }
        return nextWorkflowInstances;
    }


    private Map<String, List<Element>> getNextSteps(Element workflow, Map<String, List<Element>> nextSteps) {
        if (!nextSteps.containsKey(workflow.getName())) {
            List<Element> nextSteps1 = Collections.synchronizedList(new ArrayList<Element>(placementHelper.getNextSteps(workflow.getName())));
            nextSteps.put(workflow.getName(), nextSteps1);
        }

        return nextSteps;
    }

}
