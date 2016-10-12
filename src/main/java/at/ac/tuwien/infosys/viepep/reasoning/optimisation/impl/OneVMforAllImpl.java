package at.ac.tuwien.infosys.viepep.reasoning.optimisation.impl;

import at.ac.tuwien.infosys.viepep.database.entities.Element;
import at.ac.tuwien.infosys.viepep.database.entities.ProcessStep;
import at.ac.tuwien.infosys.viepep.database.entities.VirtualMachine;
import at.ac.tuwien.infosys.viepep.database.entities.docker.DockerContainer;
import at.ac.tuwien.infosys.viepep.reasoning.optimisation.ProcessInstancePlacementProblemService;
import net.sf.javailp.Result;
import net.sf.javailp.ResultImpl;

import java.util.*;

/**
 * Created by philippwaibel on 30/09/2016.
 */
public class OneVMforAllImpl extends AbstractDockerProvisioning implements ProcessInstancePlacementProblemService {

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

        nextSteps = new TreeMap<>(
                (o1, o2) -> {
                    return new Long(o2.getDeadline()).compareTo(new Long(o1.getDeadline()));
                });

        Map<Element, List<Element>> runningSteps = new HashMap<>();
        for (Element workflow : nextWorkflowInstances) {
            runningSteps = getRunningSteps(workflow, runningSteps);
        }

        if (runningSteps.size() == 0) {
            for (Element workflow : nextWorkflowInstances) {
                nextSteps = getNextSteps(workflow, nextSteps);
            }
            for (Map.Entry<Element, List<Element>> nextStepsEntry : nextSteps.entrySet()) {

                Element nextStep = nextStepsEntry.getValue().get(0);
                DockerContainer dockerContainer = getDockerContainer((ProcessStep) nextStep);

                List<VirtualMachine> runningVms = getRunningVms();
                VirtualMachine vm;
                if (runningVms.size() == 0) {
                    vm = startNewVm(result, dockerContainer);
                }
                else {
                    vm = runningVms.get(0);
                }

                deployDockerOnVm(result, dockerContainer, vm);
                executeServiceOnDocker(result, (ProcessStep) nextStep, dockerContainer);

            }
        }

        fillResult(result);
        result.put("tau_t_1", (tau_t.getTime() + tau_t_1) / 1000);

        return result;
    }


}
