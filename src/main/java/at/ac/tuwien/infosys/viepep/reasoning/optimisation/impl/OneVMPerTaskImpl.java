package at.ac.tuwien.infosys.viepep.reasoning.optimisation.impl;

import at.ac.tuwien.infosys.viepep.database.entities.Element;
import at.ac.tuwien.infosys.viepep.database.entities.ProcessStep;
import at.ac.tuwien.infosys.viepep.database.entities.VirtualMachine;
import at.ac.tuwien.infosys.viepep.database.entities.docker.DockerContainer;
import at.ac.tuwien.infosys.viepep.reasoning.optimisation.ProcessInstancePlacementProblemService;
import net.sf.javailp.Result;
import net.sf.javailp.ResultImpl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by philippwaibel on 30/09/2016.
 */
public class OneVMPerTaskImpl extends AbstractDockerProvisioning implements ProcessInstancePlacementProblemService {

    @Override
    public void initializeParameters() {

    }

    /**
     * 0. Get best fitting DockerContainer
     * 1. Get all running VMs
     * 2. If no VM is running
     * - Start one
     * 3. Sort VMs according to its utilization
     * 4. Iterate through available VMs (Start from fullest VM)
     * a. Check if DockerContainer fits into VM
     * b. Yes
     * - set DockerContainer VM match
     * - break
     * 5. Check if VMs can be terminated
     *
     * @param tau_t
     * @return
     */
    @Override
    public Result optimize(Date tau_t) {

        Result result = new ResultImpl();

        placementHelper.setFinishedWorkflows();

        nextWorkflowInstances = null;
        nextWorkflowInstances = getNextWorkflowInstances();

        nextSteps = new HashMap<>();

        for (Element workflow : nextWorkflowInstances) {
            nextSteps = getNextSteps(workflow, nextSteps);
        }

        for (Map.Entry<Element, List<Element>> nextStepsEntry : nextSteps.entrySet()) {
            for (Element nextStep : nextStepsEntry.getValue()) {

                DockerContainer dockerContainer = getDockerContainer((ProcessStep) nextStep);

                VirtualMachine vm = startNewVm(result, dockerContainer);
                deployDockerOnVm(result, dockerContainer, vm);
                executeServiceOnDocker(result, (ProcessStep) nextStep, dockerContainer);
            }
        }

        fillResult(result);
        result.put("tau_t_1", (tau_t.getTime() + tau_t_1) / 1000);

        return result;
    }

}
