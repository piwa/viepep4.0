package at.ac.tuwien.infosys.viepep.reasoning.service;

import at.ac.tuwien.infosys.viepep.database.entities.ProcessStep;
import at.ac.tuwien.infosys.viepep.database.entities.docker.DockerContainer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by philippwaibel on 26/09/2016.
 */
@Slf4j
@Component
public class DockerServiceExecutionController {

    @Autowired
    private LeaseDockerAndStartExecution leaseDockerAndStartExecution;

    @Async("serviceProcessExecuter")
    public void startInvocation(List<ProcessStep> processSteps) {

        final Map<DockerContainer, List<ProcessStep>> dockerProcessStepsMap = new HashMap<>();
        for (final ProcessStep processStep : processSteps) {

            processStep.setStartDate(new Date());

            DockerContainer scheduledAt = processStep.getScheduledAtDocker();
            List<ProcessStep> processStepsOnDocker = new ArrayList<>();
            if (dockerProcessStepsMap.containsKey(scheduledAt)) {
                processStepsOnDocker.addAll(dockerProcessStepsMap.get(scheduledAt));
            }
            processStepsOnDocker.add(processStep);
            dockerProcessStepsMap.put(scheduledAt, processStepsOnDocker);
        }

        for (final DockerContainer docker : dockerProcessStepsMap.keySet()) {
            final List<ProcessStep> processSteps1 = dockerProcessStepsMap.get(docker);
            if (!docker.isDeployed()) {
                docker.setDeployed(true);
                docker.setStartedAt(new Date());

                leaseDockerAndStartExecution.leaseDockerAndStartExecution(docker, processSteps1);

            } else {
                leaseDockerAndStartExecution.startExecutions(dockerProcessStepsMap.get(docker), docker);
            }
        }
    }
}
