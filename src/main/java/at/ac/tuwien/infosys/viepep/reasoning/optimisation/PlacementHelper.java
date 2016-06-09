package at.ac.tuwien.infosys.viepep.reasoning.optimisation;

import at.ac.tuwien.infosys.viepep.database.entities.Element;
import at.ac.tuwien.infosys.viepep.database.entities.ProcessStep;
import at.ac.tuwien.infosys.viepep.database.entities.VirtualMachine;
import at.ac.tuwien.infosys.viepep.database.entities.WorkflowElement;

import java.util.Date;
import java.util.List;

/**
 * Created by philippwaibel on 18/05/16.
 */
public interface PlacementHelper {

    List<Element> getRunningSteps(boolean update);

    void clear();

    List<Element> getFlattenWorkflow(List<Element> flattenWorkflowList, Element parentElement);

    List<Element> getNextSteps(String workflowInstanceId);

    List<Element> getRunningProcessSteps(String workflowInstanceID);

    long getRemainingSetupTime(String vmId, Date now);

    List<VirtualMachine> getVMs();

    void addVM(VirtualMachine vm);

    WorkflowElement getWorkflowById(String workflowInstanceId);

    void terminateVM(VirtualMachine virtualMachine);

    List<WorkflowElement> getNextWorkflowInstances();

    void addWorkflowInstance(WorkflowElement workflowElement);

    void deleteWorkflowInstance(WorkflowElement workflowElement);

    void setFinishedWorkflows();

    List<ProcessStep> getUnfinishedSteps();

    List<WorkflowElement> getAllWorkflowElements();
}
