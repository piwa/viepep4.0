package at.ac.tuwien.infosys.viepep.database.services;

import at.ac.tuwien.infosys.viepep.database.entities.Element;
import at.ac.tuwien.infosys.viepep.database.entities.WorkflowElement;
import at.ac.tuwien.infosys.viepep.database.repositories.WorkflowElementRepository;
import at.ac.tuwien.infosys.viepep.reasoning.optimisation.PlacementHelper;
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

    public void saveWorkflow(WorkflowElement workflow) {
//        log.info("Save workflowElement: " + workflow.toString());
        workflowElementRepository.save(workflow);
        placementHelperImpl.getNextWorkflowInstances(true);
    }

    public List<String> getWorkflowInstanceIds() {
        return workflowElementRepository.getWorkflowInstanceIds();
    }

    public void update(WorkflowElement workflow) {
//        log.info("Update workflowElement: " + workflow.toString());
        workflowElementRepository.save(workflow);
        placementHelperImpl.getNextWorkflowInstances(true);
    }

    public List<WorkflowElement> getAllWorkflowElementsList() {
        Iterator<WorkflowElement> iterator = workflowElementRepository.findAll().iterator();
        List<WorkflowElement> resultList = new ArrayList<>();
        while(iterator.hasNext()) {
            WorkflowElement workflowElement = iterator.next();
            loadElements(workflowElement);
            resultList.add(workflowElement);
        }

        return resultList;
    }

    public List<WorkflowElement> getList() {
        Iterator<WorkflowElement> iterator = workflowElementRepository.findAll().iterator();
        List<WorkflowElement> resultList = new ArrayList<>();
        while(iterator.hasNext()) {
            WorkflowElement workflowElement = iterator.next();
            loadElements(workflowElement);
            resultList.add(workflowElement);
        }

        return resultList;
    }

    private void loadElements(Element workflowElement) {            // TODO What the hell?
        List<Element> elements = workflowElement.getElements();
        if (elements != null) {
            for (Element element : elements) {
                loadElements(element);
            }
        }
    }

    public List<WorkflowElement> getAllWorkflowInstances() {
        return getAllWorkflowElementsList();
    }

}
