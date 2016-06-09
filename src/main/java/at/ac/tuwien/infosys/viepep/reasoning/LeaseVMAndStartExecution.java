package at.ac.tuwien.infosys.viepep.reasoning;

import at.ac.tuwien.infosys.viepep.connectors.ViePEPClientService;
import at.ac.tuwien.infosys.viepep.database.entities.ProcessStep;
import at.ac.tuwien.infosys.viepep.database.entities.ReportingAction;
import at.ac.tuwien.infosys.viepep.database.entities.VMAction;
import at.ac.tuwien.infosys.viepep.database.entities.VirtualMachine;
import at.ac.tuwien.infosys.viepep.database.services.ProcessStepDaoService;
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
 * Created by philippwaibel on 18/05/16.
 */
@Component
@Scope("prototype")
@Slf4j
public class LeaseVMAndStartExecution {

    @Autowired
    private ReportDaoService reportDaoService;
    @Autowired
    private ViePEPClientService viePEPClientService;
    @Autowired
    private ProcessStepDaoService processStepDaoService;
    @Autowired
    private ProcessExecution processExecution;

    @Value("${simulate}")
    private boolean simulate;
    @Value("${virtualmachine.startup.time}")
    private long startupTime;

    @Async
    public void leaseVMAndStartExecution(VirtualMachine virtualMachine, List<ProcessStep> processSteps1) {

        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String address = "";
        if (simulate) {
            address = "128.130.172.211";
            try {
                Thread.sleep(30000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            address = viePEPClientService.startNewVM(virtualMachine.getName(), virtualMachine.getVmType().flavor(), virtualMachine.getServiceType().name());
            try {
                Thread.sleep(startupTime); //sleep 15 seconds, since as soon as it is up, it still has to deploy the services
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        ReportingAction report =  new ReportingAction(new Date(), virtualMachine.getName(), VMAction.START);
        reportDaoService.save(report);

        if (address == null) {
            log.error("VM was not started, reset task: " + virtualMachine.getName());
            List<ProcessStep> processStepList = processStepDaoService.findByVM(virtualMachine);     // TODO is processStepList = processSteps1?
            for(ProcessStep processStep : processStepList) {
                processStep.setStartDate(null);
                processStep.setScheduled(false);
                processStep.setScheduledAtVM(null);
//                processStepDaoService.finishWorkflow(processStep);
            }
            return;
        } else {
            long time = stopWatch.getTotalTimeMillis();
            stopWatch.stop();
            virtualMachine.setStartupTime(time);
            virtualMachine.setStarted(true);
            virtualMachine.setIpAddress(address);

            startExecutions(processSteps1, virtualMachine);

        }
    }

    public void startExecutions(final List<ProcessStep> processSteps, final VirtualMachine virtualMachine) {
        for (final ProcessStep processStep : processSteps) {
            processExecution.startExecution(processStep, virtualMachine);
        }
    }

}
