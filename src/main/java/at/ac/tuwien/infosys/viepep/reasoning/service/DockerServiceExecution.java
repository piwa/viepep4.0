package at.ac.tuwien.infosys.viepep.reasoning.service;

import at.ac.tuwien.infosys.viepep.database.entities.Element;
import at.ac.tuwien.infosys.viepep.database.entities.ProcessStep;
import at.ac.tuwien.infosys.viepep.database.entities.WorkflowElement;
import at.ac.tuwien.infosys.viepep.database.entities.docker.DockerContainer;
import at.ac.tuwien.infosys.viepep.database.inmemory.services.CacheWorkflowService;
import at.ac.tuwien.infosys.viepep.reasoning.optimisation.general.PlacementHelper;
import at.ac.tuwien.infosys.viepep.reasoning.service.dto.InvocationResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * Created by philippwaibel on 18/05/16.
 */
@Component
@Scope("prototype")
@Slf4j
public class DockerServiceExecution {

    @Autowired
    private DockerServiceInvoker serviceInvoker;
    @Autowired
    private PlacementHelper placementHelper;
    @Autowired
    private CacheWorkflowService cacheWorkflowService;

    @Value("${simulate}")
    private boolean simulate;

    @Async
    public void startExecution(ProcessStep processStep, DockerContainer dockerContainer) {
        log.info("Task-Start: " + processStep);

        if (simulate) {
            try {
                Thread.sleep(processStep.getExecutionTime());
            } catch (InterruptedException e) {
            }
        } else {
            InvocationResultDTO invoke = serviceInvoker.invoke(dockerContainer, processStep);
        }

        Date finishedAt = new Date();
        processStep.setFinishedAt(finishedAt);

        log.info("Task-Done: " + processStep);

        if (processStep.isLastElement()) {

            List<Element> runningSteps = placementHelper.getRunningProcessSteps(processStep.getWorkflowName());
            List<Element> nextSteps = placementHelper.getNextSteps(processStep.getWorkflowName());
            if ((nextSteps == null || nextSteps.isEmpty()) && (runningSteps == null || runningSteps.isEmpty())) {
                WorkflowElement workflowById = cacheWorkflowService.getWorkflowById(processStep.getWorkflowName());
                workflowById.setFinishedAt(finishedAt);
                cacheWorkflowService.deleteRunningWorkflowInstance(workflowById);
                log.info("Workflow done. Workflow: " + workflowById);

            }

        }

    }

}
