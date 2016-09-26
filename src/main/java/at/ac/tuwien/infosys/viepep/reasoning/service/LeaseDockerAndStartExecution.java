package at.ac.tuwien.infosys.viepep.reasoning.service;

import at.ac.tuwien.infosys.viepep.connectors.ViePEPDockerControllerService;
import at.ac.tuwien.infosys.viepep.connectors.impl.exceptions.CouldNotStartDockerException;
import at.ac.tuwien.infosys.viepep.database.entities.ProcessStep;
import at.ac.tuwien.infosys.viepep.database.entities.ReportingAction;
import at.ac.tuwien.infosys.viepep.database.entities.VMDockerAction;
import at.ac.tuwien.infosys.viepep.database.entities.docker.DockerContainer;
import at.ac.tuwien.infosys.viepep.database.services.ReportDaoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.Date;
import java.util.List;

/**
 * Created by philippwaibel on 26/09/2016.
 */
@Component
@Scope("prototype")
@Slf4j
public class LeaseDockerAndStartExecution {

    @Autowired
    private ReportDaoService reportDaoService;
    @Autowired
    private ViePEPDockerControllerService viePEPDockerClientService;
    @Autowired
    private DockerServiceExecution serviceExecution;

    @Value("${simulate}")
    private boolean simulate;
    @Value("${virtualmachine.startup.time}")
    private long startupTime;

    @Async
    public void leaseDockerAndStartExecution(DockerContainer dockerContainer, List<ProcessStep> processSteps) {

        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        if (simulate) {
            try {
                Thread.sleep(30000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            try {
                dockerContainer = viePEPDockerClientService.startDocker(dockerContainer.getVirtualMachines().get(0), dockerContainer);
                log.info("Docker up and running with ip: " + dockerContainer.getIpAddress() + " dockerContainer: " + dockerContainer);
                try {
                    Thread.sleep(startupTime); //sleep 15 seconds, since as soon as it is up, it still has to deploy the services
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (CouldNotStartDockerException e) {
                log.error("EXCEPTION", e);
                dockerContainer = null;
            }

        }


        ReportingAction report =  new ReportingAction(new Date(), dockerContainer.getName(), VMDockerAction.START);
        reportDaoService.save(report);

        if (dockerContainer == null) {
            log.error("Docker " + dockerContainer.getName() + " was not started, reset task");
            for(ProcessStep processStep : processSteps) {
                processStep.setStartDate(null);
                processStep.setScheduled(false);
                processStep.setScheduledAtVM(null);
            }
            return;
        } else {
            long time = stopWatch.getTotalTimeMillis();
            stopWatch.stop();
            dockerContainer.setStartupTime(time);
            dockerContainer.setStarted(true);

            startExecutions(processSteps, dockerContainer);

        }
    }

    public void startExecutions(final List<ProcessStep> processSteps, final DockerContainer dockerContainer) {
        for (final ProcessStep processStep : processSteps) {
            serviceExecution.startExecution(processStep, dockerContainer);
        }
    }

}

