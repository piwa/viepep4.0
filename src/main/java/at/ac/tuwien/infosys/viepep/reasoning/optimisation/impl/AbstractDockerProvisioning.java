package at.ac.tuwien.infosys.viepep.reasoning.optimisation.impl;

import at.ac.tuwien.infosys.viepep.database.entities.*;
import at.ac.tuwien.infosys.viepep.database.entities.docker.DockerContainer;
import at.ac.tuwien.infosys.viepep.database.inmemory.services.CacheDockerService;
import at.ac.tuwien.infosys.viepep.database.inmemory.services.CacheVirtualMachineService;
import at.ac.tuwien.infosys.viepep.database.inmemory.services.CacheWorkflowService;
import at.ac.tuwien.infosys.viepep.reasoning.optimisation.PlacementHelper;
import net.sf.javailp.Result;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Created by philippwaibel on 12/10/2016.
 */
public abstract class AbstractDockerProvisioning {

    @Autowired
    protected PlacementHelper placementHelper;
    @Autowired
    protected CacheWorkflowService cacheWorkflowService;
    @Autowired
    protected CacheDockerService cacheDockerService;
    @Autowired
    protected CacheVirtualMachineService cacheVirtualMachineService;

    protected List<WorkflowElement> nextWorkflowInstances;
    protected Map<Element, List<Element>> nextSteps;

    protected VMType defaultVM = VMType.AWS_QUAD_CORE;
    protected long tau_t_1 = 30 * 1000;


    protected void sortRunningVmsExecutionTime(List<VirtualMachine> runningVms) {
        Date currentTime = new Date();
        runningVms.sort((vm1, vm2) -> new Long(placementHelper.getRemainingLeasingDuration(currentTime, vm1)).compareTo(new Long(placementHelper.getRemainingLeasingDuration(currentTime, vm2))));
    }

    protected void executeServiceOnDocker(Result result, ProcessStep nextStep, DockerContainer dockerContainer) {
        String decisionVariableX = placementHelper.getDecisionVariableX(nextStep, dockerContainer);
        result.put(decisionVariableX, 1);
    }

    protected void deployDockerOnVm(Result result, DockerContainer dockerContainer, VirtualMachine vm) {
        String decisionVariableA = placementHelper.getDecisionVariableA(dockerContainer, vm);
        result.put(decisionVariableA, 1);
    }


    protected VirtualMachine startNewVm(Result result, DockerContainer dockerContainer) {

        // TODO check if defaultVM is suitable for at least one DockerContainer

        List<VirtualMachine> virtualMachineList = cacheVirtualMachineService.getVMs(defaultVM);

        VirtualMachine vm = virtualMachineList.get(0);      // check if available
        result.put("y_" + vm.getName(), 1);

        return vm;
    }

    protected void fillResult(Result result) {
        for (VirtualMachine vm : cacheVirtualMachineService.getAllVMs()) {
            String y_v_k = placementHelper.getDecisionVariableY(vm);
            if (!result.containsVar(y_v_k)) {
                result.put(y_v_k, 0);
            }
        }


        for (Element runningStep : placementHelper.getRunningSteps()) {
            DockerContainer container = ((ProcessStep) runningStep).getScheduledAtContainer();
            VirtualMachine vm = container.getVirtualMachine();
            String decisionVariableA = placementHelper.getDecisionVariableA(container, vm);
            if (!result.containsVar(decisionVariableA)) {
                result.put(decisionVariableA, 1);
            }

        }

        for (DockerContainer container : cacheDockerService.getAllDockerContainers()) {
            VirtualMachine vm = container.getVirtualMachine();
            if (vm != null) {
                String decisionVariableA = placementHelper.getDecisionVariableA(container, vm);
                if (!result.containsVar(decisionVariableA)) {
                    result.put(decisionVariableA, 0);
                }
            }
        }
    }

    protected DockerContainer getDockerContainer(ProcessStep nextStep) {
        DockerContainer dockerContainer = null;
        for (DockerContainer currentContainer : cacheDockerService.getDockerContainers(nextStep)) {
            if (dockerContainer == null) {
                dockerContainer = currentContainer;
            }
            else if (dockerContainer.getContainerConfiguration().getCPUPoints() > currentContainer.getContainerConfiguration().getCPUPoints() ||
                    dockerContainer.getContainerConfiguration().getRAM() > currentContainer.getContainerConfiguration().getRAM()) {
                dockerContainer = currentContainer;
            }
        }
        return dockerContainer;
    }


    protected List<WorkflowElement> getNextWorkflowInstances() {
        if (nextWorkflowInstances == null) {
            nextWorkflowInstances = Collections.synchronizedList(new ArrayList<WorkflowElement>(cacheWorkflowService.getRunningWorkflowInstances()));
        }
        return nextWorkflowInstances;
    }


    protected Map<Element, List<Element>> getNextSteps(Element workflow, Map<Element, List<Element>> nextSteps) {
        if (!nextSteps.containsKey(workflow.getName())) {
            List<Element> nextSteps1 = Collections.synchronizedList(new ArrayList<Element>(placementHelper.getNextSteps(workflow.getName())));
            nextSteps.put(workflow, nextSteps1);
        }

        return nextSteps;
    }

    protected Map<Element, List<Element>> getRunningSteps(Element workflow, Map<Element, List<Element>> runningSteps) {
        if (!runningSteps.containsKey(workflow.getName())) {
            List<Element> nextSteps1 = Collections.synchronizedList(new ArrayList<Element>(placementHelper.getRunningProcessSteps(workflow.getName())));
            runningSteps.put(workflow, nextSteps1);
        }

        return runningSteps;
    }


    protected List<VirtualMachine> getRunningVms() {
        return new ArrayList<>(cacheVirtualMachineService.getStartedAndScheduledForStartVMs());
    }
}
