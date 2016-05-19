package at.ac.tuwien.infosys.viepep.reasoning;

import at.ac.tuwien.infosys.viepep.database.entities.ProcessStep;
import at.ac.tuwien.infosys.viepep.database.entities.VirtualMachine;
import at.ac.tuwien.infosys.viepep.database.entities.WorkflowElement;
import at.ac.tuwien.infosys.viepep.database.services.ProcessStepDaoService;
import at.ac.tuwien.infosys.viepep.database.services.WorkflowDaoService;
import at.ac.tuwien.infosys.viepep.reasoning.dto.InvocationResultDTO;
import at.ac.tuwien.infosys.viepep.reasoning.optimisation.PlacementHelper;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by philippwaibel on 18/05/16.
 */
@Component
@Scope("prototype")
@Log4j
public class ProcessExecution {

    @Autowired
    private ServiceInvoker serviceInvoker;
    @Autowired
    private PlacementHelper placementHelper;
    @Autowired
    private WorkflowDaoService workflowDaoService;
    @Autowired
    private ProcessStepDaoService processStepDaoService;

    @Value("${simulate}")
    private boolean simulate;

    @Async
    public void startExecution(ProcessStep processStep, VirtualMachine virtualMachine) {
        log.info("Task-Start: " + processStep);
        if (simulate) {
            try {
                Thread.sleep(processStep.getExecutionTime());
            } catch (InterruptedException e) {
            }
        } else {
            InvocationResultDTO invoke = serviceInvoker.invoke(virtualMachine, processStep);
        }

        Date finishedAt = new Date();
        processStep.setFinishedAt(finishedAt);
        processStepDaoService.update(processStep);
        if (processStep.isLastElement()) {
            log.info("Workflow done. Workflow Name: " + processStep.getWorkflowName());
            WorkflowElement workflowById = placementHelper.getWorkflowById(processStep.getWorkflowName());
            workflowById.setFinishedAt(finishedAt);
            workflowDaoService.update(workflowById);
        }
/*        else {
            processStepDaoService.update(processStep);
        }
*/
        log.info("Task-Done: " + processStep);
    }

}
