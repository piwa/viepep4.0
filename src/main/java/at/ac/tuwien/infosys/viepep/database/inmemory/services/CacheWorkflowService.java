package at.ac.tuwien.infosys.viepep.database.inmemory.services;

import at.ac.tuwien.infosys.viepep.database.entities.WorkflowElement;
import at.ac.tuwien.infosys.viepep.database.inmemory.database.InMemoryCacheImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by philippwaibel on 10/06/16.
 */
@Component
public class CacheWorkflowService {

    @Autowired
    private InMemoryCacheImpl inMemoryCache;


    public synchronized List<WorkflowElement> getRunningWorkflowInstances() {
        List<WorkflowElement> workflows = Collections.synchronizedList(inMemoryCache.getRunningWorkflows());
        List<WorkflowElement> returnList = new ArrayList<>();
        Iterator<WorkflowElement> iterator = workflows.iterator();
        while(iterator.hasNext()) {
            WorkflowElement workflow = iterator.next();
            if(workflow.getFinishedAt() == null) {
                returnList.add(workflow);
            }
        }

        return returnList;
    }


    public void addWorkflowInstance(WorkflowElement workflowElement) {
        inMemoryCache.addRunningWorkflow(workflowElement);
        inMemoryCache.addToAllWorkflows(workflowElement);
    }


    public void deleteRunningWorkflowInstance(WorkflowElement workflowElement) {
        inMemoryCache.getRunningWorkflows().remove(workflowElement);
    }


    public List<WorkflowElement> getAllWorkflowElements() {
        return inMemoryCache.getAllWorkflowInstances();
    }


    public synchronized WorkflowElement getWorkflowById(String workflowInstanceId) {
        List<WorkflowElement> nextWorkflows = inMemoryCache.getRunningWorkflows();
        Iterator<WorkflowElement> iterator = nextWorkflows.iterator();

        while(iterator.hasNext()) {
            WorkflowElement nextWorkflow = iterator.next();
            if (nextWorkflow.getName().equals(workflowInstanceId)) {
                return nextWorkflow;
            }
        }
        return null;
    }




}
