package at.ac.tuwien.infosys.viepep.reasoning.optimisation.impl;

import at.ac.tuwien.infosys.viepep.database.entities.*;
import at.ac.tuwien.infosys.viepep.database.entities.docker.DockerContainer;
import at.ac.tuwien.infosys.viepep.database.inmemory.services.CacheDockerService;
import at.ac.tuwien.infosys.viepep.database.inmemory.services.CacheVirtualMachineService;
import at.ac.tuwien.infosys.viepep.database.inmemory.services.CacheWorkflowService;
import at.ac.tuwien.infosys.viepep.reasoning.optimisation.PlacementHelper;
import at.ac.tuwien.infosys.viepep.reasoning.optimisation.ProcessInstancePlacementProblemService;
import net.sf.javailp.Result;
import net.sf.javailp.ResultImpl;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Created by philippwaibel on 30/09/2016.
 */
public class SimpleDockerProcessInstancePlacementProblemServiceImpl implements ProcessInstancePlacementProblemService {


    @Autowired
    private PlacementHelper placementHelper;
    @Autowired
    private CacheWorkflowService cacheWorkflowService;
    @Autowired
    private CacheDockerService cacheDockerService;
    @Autowired
    private CacheVirtualMachineService cacheVirtualMachineService;

    private List<WorkflowElement> nextWorkflowInstances;
    private Map<String, List<Element>> nextSteps;

    private VMType defaultVM = VMType.AWS_QUAD_CORE;

    private long tau_t_1 = 60 * 1000;


    @Override
    public void initializeParameters() {

    }

    /**
     * 0. Get best fitting DockerContainer
     * 1. Get all running VMs
     * 2. If no VM is running
     *      - Start one
     * 3. Sort VMs according to its utilization
     * 4. Iterate through available VMs (Start from fullest VM)
     *      a. Check if DockerContainer fits into VM
     *      b. Yes
     *          - set DockerContainer VM match
     *          - break
     * 5. Check if VMs can be terminated
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

        for(Map.Entry<String, List<Element>> nextStepsEntry : nextSteps.entrySet()) {
            for (Element nextStep : nextStepsEntry.getValue()) {

                DockerContainer dockerContainer = getDockerContainer((ProcessStep) nextStep);

                List<VirtualMachine> runningVms = getRunningVms();

                if(runningVms.size() == 0) {
                    VirtualMachine vm = startNewVm(result, dockerContainer);
                    deployDockerOnVm(result, dockerContainer, vm);
                    executeServiceOnDocker(result, (ProcessStep) nextStep, dockerContainer);
                }
                else {
                    sortRunningVmsList(runningVms);
                    VirtualMachine vm = findBestFittingVm(result, runningVms, dockerContainer);
                    deployDockerOnVm(result, dockerContainer, vm);
                    executeServiceOnDocker(result, (ProcessStep) nextStep, dockerContainer);
                }
            }
        }

        fillResult(result);
        result.put("tau_t_1", (tau_t.getTime() + tau_t_1)/1000);

        return result;
    }

    private void fillResult(Result result) {
        for(VirtualMachine vm : cacheVirtualMachineService.getAllVMs()) {
            String y_v_k = placementHelper.getDecisionVariableY(vm);
            if(!result.containsVar(y_v_k)) {
                result.put(y_v_k,0);
            }
        }


        for(Element runningStep : placementHelper.getRunningSteps()) {
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

    private VirtualMachine findBestFittingVm(Result result, List<VirtualMachine> runningVms, DockerContainer dockerContainer) {

        for(VirtualMachine runningVm : runningVms) {


            List<DockerContainer> deployedContainer = runningVm.getDeployedContainers();
            double usedCPUPoints = deployedContainer.stream().mapToDouble(c -> c.getContainerConfiguration().getCPUPoints()).sum();
            double usedRAMPoints = deployedContainer.stream().mapToDouble(c -> c.getContainerConfiguration().getRAM()).sum();

            double freeCPUPoints = runningVm.getVmType().getCpuPoints() - usedCPUPoints;
            double freeRAMPoints = runningVm.getVmType().getRamPoints() - usedRAMPoints;

            if(freeCPUPoints >= dockerContainer.getContainerConfiguration().getCPUPoints() && freeRAMPoints >= dockerContainer.getContainerConfiguration().getRAM()) {
                return runningVm;
            }

        }

        VirtualMachine newVm = startNewVm(result, dockerContainer);
        return newVm;

    }

    private void executeServiceOnDocker(Result result, ProcessStep nextStep, DockerContainer dockerContainer) {
        String decisionVariableX = placementHelper.getDecisionVariableX(nextStep, dockerContainer);
        result.put(decisionVariableX, 1);
    }

    private void deployDockerOnVm(Result result, DockerContainer dockerContainer, VirtualMachine vm) {
        String decisionVariableA = placementHelper.getDecisionVariableA(dockerContainer, vm);
        result.put(decisionVariableA, 1);
    }

    private void sortRunningVmsList(List<VirtualMachine> runningVms) {
        // TODO
    }

    private VirtualMachine startNewVm(Result result, DockerContainer dockerContainer) {

        // TODO check if defaultVM is suitable for at least one DockerContainer

        List<VirtualMachine> virtualMachineList = cacheVirtualMachineService.getVMs(defaultVM);

        VirtualMachine vm = virtualMachineList.get(0);      // check if available
        result.put("y_" + vm.getName(), 1);

        return vm;
    }

    private DockerContainer getDockerContainer(ProcessStep nextStep) {
        DockerContainer dockerContainer = null;
        for (DockerContainer currentContainer : cacheDockerService.getDockerContainers(nextStep)) {
            if(dockerContainer == null) {
                dockerContainer = currentContainer;
            }
            else if(dockerContainer.getContainerConfiguration().getCPUPoints() > currentContainer.getContainerConfiguration().getCPUPoints() ||
                    dockerContainer.getContainerConfiguration().getRAM() > currentContainer.getContainerConfiguration().getRAM()) {
                dockerContainer = currentContainer;
            }
        }
        return dockerContainer;
    }


    public List<WorkflowElement> getNextWorkflowInstances() {
        if (nextWorkflowInstances == null) {
            nextWorkflowInstances = Collections.synchronizedList(new ArrayList<WorkflowElement>(cacheWorkflowService.getRunningWorkflowInstances()));
        }
        return nextWorkflowInstances;
    }


    private Map<String, List<Element>> getNextSteps(Element workflow, Map<String, List<Element>> nextSteps) {
        if (!nextSteps.containsKey(workflow.getName())) {
            List<Element> nextSteps1 = Collections.synchronizedList(new ArrayList<Element>(placementHelper.getNextSteps(workflow.getName())));
            nextSteps.put(workflow.getName(), nextSteps1);
        }

        return nextSteps;
    }


    private List<VirtualMachine> getRunningVms() {
        return new ArrayList<>(cacheVirtualMachineService.getStartedAndScheduledForStartVMs());
    }
}
