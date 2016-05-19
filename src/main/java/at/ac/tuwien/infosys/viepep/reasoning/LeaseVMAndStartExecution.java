package at.ac.tuwien.infosys.viepep.reasoning;

import at.ac.tuwien.infosys.viepep.connectors.ViePEPClientService;
import at.ac.tuwien.infosys.viepep.database.entities.ProcessStep;
import at.ac.tuwien.infosys.viepep.database.entities.ReportingAction;
import at.ac.tuwien.infosys.viepep.database.entities.VMAction;
import at.ac.tuwien.infosys.viepep.database.entities.VirtualMachine;
import at.ac.tuwien.infosys.viepep.database.services.ProcessStepDaoService;
import at.ac.tuwien.infosys.viepep.database.services.ReportDaoService;
import at.ac.tuwien.infosys.viepep.database.services.VirtualMachineDaoService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by philippwaibel on 18/05/16.
 */
@Component
@Scope("prototype")
@Log4j
public class LeaseVMAndStartExecution {

    @Autowired
    private ReportDaoService reportDaoService;
    @Autowired
    private ViePEPClientService viePEPClientService;
    @Autowired
    private VirtualMachineDaoService virtualMachineDaoService;
    @Autowired
    private ProcessStepDaoService processStepDaoService;
    @Autowired
    private ProcessExecution processExecution;

    @Value("${simulate}")
    private boolean simulate;
    @Value("${virtualmachine.startup.time}")
    private long startupTime;


//    @Override
//    public String call() throws Exception {
    @Async
    public Future<String> leaseVMAndStartExecution(VirtualMachine virtualMachine, List<ProcessStep> processSteps1) {

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


        ReportingAction report = new ReportingAction(new Date(), virtualMachine.getName(), VMAction.START);
        reportDaoService.save(report);

        if (address == null) {

//                            throw new Exception("server could not be started, rollback");
            log.error("VM was not started, reset task: " + virtualMachine.getName());
            List<ProcessStep> processStepList = processStepDaoService.findByVM(virtualMachine);
//                            for (ProcessStep processStep : virtualMachine.getAssignedSteps()) {
            for(ProcessStep processStep : processStepList) {
                processStep.setStartDate(null);
                processStep.setScheduled(false);
                processStep.setScheduledAtVM(null);
                processStepDaoService.update(processStep);
            }
            return null;
        } else {
//                            Thread.sleep(30000);
            long time = stopWatch.getTotalTimeMillis();
            stopWatch.stop();
            virtualMachine.setStartupTime(time);
            virtualMachine.setStarted(true);
            virtualMachine.setIpAddress(address);

            virtualMachineDaoService.update(virtualMachine);
            startExecutions(processSteps1, virtualMachine);

            return new AsyncResult<String>(address);
        }
    }

    public void startExecutions(final List<ProcessStep> processSteps, final VirtualMachine virtualMachine) {
        for (final ProcessStep processStep : processSteps) {
            processExecution.startExecution(processStep, virtualMachine);
        }
    }

}
