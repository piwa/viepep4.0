package at.ac.tuwien.infosys.viepep.database.services;

import at.ac.tuwien.infosys.viepep.database.entities.Element;
import at.ac.tuwien.infosys.viepep.database.entities.ProcessStep;
import at.ac.tuwien.infosys.viepep.database.entities.VirtualMachine;
import at.ac.tuwien.infosys.viepep.database.entities.WorkflowElement;
import at.ac.tuwien.infosys.viepep.database.repositories.WorkflowElementRepository;
import at.ac.tuwien.infosys.viepep.reasoning.optimisation.general.PlacementHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by philippwaibel on 17/05/16.
 */
@Component
@Slf4j
public class WorkflowDaoService {

    @Autowired
    private WorkflowElementRepository workflowElementRepository;
    @Autowired
    private PlacementHelper placementHelperImpl;
    @Autowired
    private VirtualMachineDaoService virtualMachineDaoService;

    public WorkflowElement finishWorkflow(WorkflowElement workflow) {
        log.info("-- Update workflowElement: " + workflow.toString());

        List<Element> flattedWorkflow = placementHelperImpl.getFlattenWorkflow(new ArrayList<>(), workflow);
        Date finishedDate = getFinishedDate(flattedWorkflow);

        workflow.setFinishedAt(finishedDate);
        for(Element element : flattedWorkflow) {
            if (element.getFinishedAt() == null) {
                element.setFinishedAt(workflow.getFinishedAt());            // TODO can be deleted?
            }
            if(element instanceof ProcessStep) {

                VirtualMachine vm = ((ProcessStep) element).getScheduledAtVM();
                if(vm != null) {                    // if the process step is after an XOR the process steps on one side of the XOR are not executed
                    if(vm.getId() != null) {
                        vm = virtualMachineDaoService.getVm(vm);
                        ((ProcessStep) element).setScheduledAtVM(vm);
                        virtualMachineDaoService.update(vm);
                    }
                    else {
                        vm = virtualMachineDaoService.update(vm);
                        ((ProcessStep) element).setScheduledAtVM(vm);
                    }
                }

            }

        }
        return workflowElementRepository.save(workflow);
    }

    private Date getFinishedDate(List<Element> flattedWorkflow) {
        Date finishedDate = null;
        for(Element element : flattedWorkflow) {
            if(element instanceof ProcessStep && element.isLastElement()) {
                if(element.getFinishedAt() != null) {
                    if(finishedDate == null) {
                        finishedDate = element.getFinishedAt();
                    }
                    else if(element.getFinishedAt().after(finishedDate)) {
                        finishedDate = element.getFinishedAt();
                    }
                }
            }
        }
        return finishedDate;
    }


}
