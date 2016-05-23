package at.ac.tuwien.infosys.viepep.reasoning;

import at.ac.tuwien.infosys.viepep.database.entities.Element;
import at.ac.tuwien.infosys.viepep.database.entities.ProcessStep;
import at.ac.tuwien.infosys.viepep.database.entities.VirtualMachine;
import at.ac.tuwien.infosys.viepep.database.entities.WorkflowElement;
import at.ac.tuwien.infosys.viepep.database.services.ElementDaoService;
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
import java.util.List;

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
    private ElementDaoService elementDaoService;
    @Autowired
    private ProcessStepDaoService processStepDaoService;

    @Value("${simulate}")
    private boolean simulate;

    @Async
    public void startExecution(ProcessStep processStep, VirtualMachine virtualMachine) {
        log.info("Task-Start: " + processStep);
//        processStep.setStartDate(new Date());
        processStepDaoService.update(processStep);
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
        log.info("Task-Done: " + processStep);

        if (processStep.isLastElement()) {
            log.info("Workflow done. Workflow Name: " + processStep.getWorkflowName());
            WorkflowElement workflowById = placementHelper.getWorkflowById(processStep.getWorkflowName());
            List<Element> nextSteps = placementHelper.getNextSteps(processStep.getWorkflowName());
            if(nextSteps == null || nextSteps.isEmpty()) {
                workflowById.setFinishedAt(finishedAt);                             // TODO nullpointer if there are more than one end element
                workflowDaoService.update(workflowById);
            }

//            workflowById.getElements().get(0).setFinishedAt(finishedAt);        // TODO set all finishedAT of child elements of workflowByID
//            elementDaoService.update(workflowById.getElements().get(0));


        }
/*        else {
            processStepDaoService.update(processStep);
        }
*/

    }

}
