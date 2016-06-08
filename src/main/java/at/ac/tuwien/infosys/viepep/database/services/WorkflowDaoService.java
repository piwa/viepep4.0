package at.ac.tuwien.infosys.viepep.database.services;

import at.ac.tuwien.infosys.viepep.database.entities.WorkflowElement;
import at.ac.tuwien.infosys.viepep.database.repositories.WorkflowElementRepository;
import at.ac.tuwien.infosys.viepep.reasoning.optimisation.PlacementHelper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    public void saveWorkflow(WorkflowElement workflow) {
//        log.info("Save workflowElement: " + workflow.toString());
        workflowElementRepository.save(workflow);
        placementHelperImpl.getNextWorkflowInstances(true);
    }


    public void update(WorkflowElement workflow) {
//        log.info("Update workflowElement: " + workflow.toString());
        workflowElementRepository.save(workflow);
        placementHelperImpl.getNextWorkflowInstances(true);
    }

    public List<WorkflowElement> getAllWorkflowElementsList() {
        Iterator<WorkflowElement> iterator = workflowElementRepository.findAll().iterator();


        List<WorkflowElement> resultList = Lists.newArrayList(iterator);

        return resultList;
    }

}
