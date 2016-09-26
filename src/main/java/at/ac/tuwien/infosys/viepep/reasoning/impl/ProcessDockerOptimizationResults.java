package at.ac.tuwien.infosys.viepep.reasoning.impl;

import at.ac.tuwien.infosys.viepep.database.entities.Element;
import at.ac.tuwien.infosys.viepep.database.entities.ProcessStep;
import at.ac.tuwien.infosys.viepep.database.entities.WorkflowElement;
import at.ac.tuwien.infosys.viepep.database.entities.docker.DockerConfiguration;
import at.ac.tuwien.infosys.viepep.database.entities.docker.DockerContainer;
import at.ac.tuwien.infosys.viepep.database.entities.docker.DockerImage;
import at.ac.tuwien.infosys.viepep.database.inmemory.services.CacheDockerService;
import at.ac.tuwien.infosys.viepep.database.inmemory.services.CacheWorkflowService;
import at.ac.tuwien.infosys.viepep.reasoning.optimisation.docker.DockerOptimizationResult;
import at.ac.tuwien.infosys.viepep.reasoning.optimisation.general.PlacementHelper;
import at.ac.tuwien.infosys.viepep.reasoning.service.DockerServiceExecutionController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by philippwaibel on 26/09/2016.
 */
@Slf4j
@Component
@Scope("prototype")
public class ProcessDockerOptimizationResults {

    @Autowired
    private PlacementHelper placementHelper;
    @Autowired
    private DockerServiceExecutionController serviceExecutionController;
    @Autowired
    private CacheDockerService cacheDockerService;
    @Autowired
    private CacheWorkflowService cacheWorkflowService;

    @Async
    public void processResults(DockerOptimizationResult optimize, Date tau_t) {


        List<DockerContainer> dockersToStart = new ArrayList<>();
        //set steps to be scheduled
        List<ProcessStep> scheduledForExecution = new ArrayList<>();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("------------------------- Dockers running ----------------------------\n");
        List<DockerContainer> dockerContainers = cacheDockerService.getDockerContainers();
        for(DockerContainer dockerContainer : dockerContainers) {
            if(dockerContainer.isDeployed() && dockerContainer.isStarted()) {
                stringBuilder.append(dockerContainers.toString()).append("\n");
            }
        }

        List<WorkflowElement> allWorkflowInstances = cacheWorkflowService.getRunningWorkflowInstances();
        stringBuilder.append("------------------------ Tasks running ---------------------------\n");
        List<ProcessStep> unfinishedNextSteps = placementHelper.getUnfinishedSteps();

        getRunningTasks(optimize, tau_t, dockersToStart, scheduledForExecution, stringBuilder, allWorkflowInstances, unfinishedNextSteps);

        stringBuilder.append("-------------------------- Optimization results -----------------------------\n");
        for (Map.Entry<ProcessStep, DockerConfiguration> resultEntry : optimize.getProcessStepDockerConfigMap().entrySet()) {
            stringBuilder.append(resultEntry.getKey().getName()).append(" -> ").append(resultEntry.getValue()).append("\n");
        }
        stringBuilder.append("----------- Container should be used (running or has to be started): ----\n");
        for (DockerContainer dockerContainer : dockersToStart) {
            stringBuilder.append(dockerContainer).append("\n");
        }

        stringBuilder.append("----------------------- Tasks to be started ----------------------\n");
        for (ProcessStep processStep : scheduledForExecution) {
            stringBuilder.append("Task-TODO: ").append(processStep).append("\n");
        }
        stringBuilder.append("------------------------------------------------------------------\n");
        log.info(stringBuilder.toString().replaceAll("(\r\n|\n)", "\r\n                                                                                                     "));

        serviceExecutionController.startInvocation(scheduledForExecution);

        cleanupVMs(tau_t);
    }

    private void getRunningTasks(DockerOptimizationResult optimize, Date tau_t, List<DockerContainer> dockersToStart, List<ProcessStep> scheduledForExecution, StringBuilder stringBuilder, List<WorkflowElement> allWorkflowInstances, List<ProcessStep> unfinishedNextSteps) {
        for (Element workflow : allWorkflowInstances) {
            List<Element> runningSteps = placementHelper.getRunningProcessSteps(workflow.getName());
            for (Element runningStep : runningSteps) {
                if(((ProcessStep) runningStep).getScheduledAtDocker().isStarted()) {
                    stringBuilder.append("Task-Running: ").append(runningStep).append("\n");
                }
            }

            for(Map.Entry<ProcessStep, DockerConfiguration> resultEntry : optimize.getProcessStepDockerConfigMap().entrySet()) {
                DockerConfiguration dockerConfiguration = resultEntry.getValue();
                DockerImage dockerImage = cacheDockerService.parseByAppId(resultEntry.getKey().getServiceType().getName());
                DockerContainer dockerContainer = new DockerContainer(dockerImage, dockerConfiguration);
                dockersToStart.add(dockerContainer);

                resultEntry.getKey().setScheduledForExecution(true, tau_t);
                resultEntry.getKey().setScheduledAtDocker(dockerContainer);
                scheduledForExecution.add(resultEntry.getKey());

            }
            /*
            for (ProcessStep processStep : unfinishedNextSteps) {
                if (!processStep.getWorkflowName().equals(workflow.getName())) {
                    continue;
                }

                processXYValues(optimize, tau_t, dockersToStart, scheduledForExecution, processStep);
            }
*/
        }
    }

    /**
     * Check if step has to be started
     * @param optimize
     * @param tau_t
     * @param dockersToStart
     * @param scheduledForExecution
     * @param processStep
     */
    private void processXYValues(DockerOptimizationResult optimize, Date tau_t, List<DockerContainer> dockersToStart, List<ProcessStep> scheduledForExecution, ProcessStep processStep) {


        for(Map.Entry<ProcessStep, DockerConfiguration> resultEntry : optimize.getProcessStepDockerConfigMap().entrySet()) {
            DockerConfiguration dockerConfiguration = resultEntry.getValue();
            DockerImage dockerImage = cacheDockerService.parseByAppId(resultEntry.getKey().getServiceType().getName());
            DockerContainer dockerContainer = new DockerContainer(dockerImage, dockerConfiguration);
            dockersToStart.add(dockerContainer);

            processStep.setScheduledForExecution(true, tau_t);
            processStep.setScheduledAtDocker(dockerContainer);
            scheduledForExecution.add(processStep);

        }

    }

    private void cleanupVMs(Date tau_t_0) {
        List<DockerContainer> dockerContainers = cacheDockerService.getDockerContainers();
        for (DockerContainer dockerContainer : dockerContainers) {
            if (dockerContainer.isCanBeTermianted()) {
                placementHelper.terminateDockerContainer(dockerContainer);
            }
        }
    }

}
