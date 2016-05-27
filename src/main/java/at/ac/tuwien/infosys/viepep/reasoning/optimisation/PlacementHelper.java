package at.ac.tuwien.infosys.viepep.reasoning.optimisation;

import at.ac.tuwien.infosys.viepep.database.entities.Element;
import at.ac.tuwien.infosys.viepep.database.entities.VirtualMachine;
import at.ac.tuwien.infosys.viepep.database.entities.WorkflowElement;

import java.util.Date;
import java.util.List;

/**
 * Created by philippwaibel on 18/05/16.
 */
public interface PlacementHelper {

    List<WorkflowElement> getNextWorkflowInstances(boolean cleanup);

    List<Element> getRunningSteps(boolean update);

    void clear();

    List<Element> getNextSteps(String workflowInstanceId);

    List<Element> getRunningProcessSteps(String workflowInstanceID);

    long getRemainingSetupTime(String vmId, Date now);

    List<VirtualMachine> getVMs(boolean update);

    WorkflowElement getWorkflowById(String workflowInstanceId);

    void terminateVM(VirtualMachine virtualMachine);

    void setFinishedWorkflows();
}
