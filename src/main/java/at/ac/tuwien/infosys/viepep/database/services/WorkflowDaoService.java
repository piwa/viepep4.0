package at.ac.tuwien.infosys.viepep.database.services;

import at.ac.tuwien.infosys.viepep.database.entities.Element;
import at.ac.tuwien.infosys.viepep.database.entities.ProcessStep;
import at.ac.tuwien.infosys.viepep.database.entities.VirtualMachine;
import at.ac.tuwien.infosys.viepep.database.entities.WorkflowElement;
import at.ac.tuwien.infosys.viepep.database.repositories.WorkflowElementRepository;
import at.ac.tuwien.infosys.viepep.reasoning.optimisation.PlacementHelper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
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
    private ElementDaoService elementDaoService;
    @Autowired
    private VirtualMachineDaoService virtualMachineDaoService;

    public void saveWorkflow(WorkflowElement workflow) {
//        log.info("Save workflowElement: " + workflow.toString());
//        workflowElementRepository.save(workflow);
        placementHelperImpl.addWorkflowInstance(workflow);
    }


    public void finishWorkflow(WorkflowElement workflow) {
//        log.info("Update workflowElement: " + workflow.toString());
        for(Element element : placementHelperImpl.getFlattenWorkflow(new ArrayList<>(), workflow)) {
            if (element.getFinishedAt() == null) {
                element.setFinishedAt(workflow.getFinishedAt());
            }
            if(element instanceof ProcessStep) {

                VirtualMachine vm = ((ProcessStep) element).getScheduledAtVM();
                if(vm != null) {                    // if the process step is after an XOR the process steps on one side of the XOR are not used
                    vm = virtualMachineDaoService.getVm(vm);
                    ((ProcessStep) element).setScheduledAtVM(vm);
                    virtualMachineDaoService.update(vm);
                }
            }
//            elementDaoService.update(element);
        }
        workflowElementRepository.save(workflow);

        placementHelperImpl.deleteWorkflowInstance(workflow);
    }

    public List<WorkflowElement> getAllWorkflowElementsList() {
        Iterator<WorkflowElement> iterator = workflowElementRepository.findAll().iterator();


        List<WorkflowElement> resultList = Lists.newArrayList(iterator);

        return resultList;
    }

}
