package at.ac.tuwien.infosys.viepep.reasoning;

import at.ac.tuwien.infosys.viepep.database.entities.ProcessStep;
import at.ac.tuwien.infosys.viepep.database.entities.VirtualMachine;
import at.ac.tuwien.infosys.viepep.database.services.ProcessStepDaoService;
import at.ac.tuwien.infosys.viepep.database.services.VirtualMachineDaoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.Future;

/**
 * Created by philippwaibel on 18/05/16.
 */
@Slf4j
@Component
public class ProcessInvocation {


    @Autowired
    private VirtualMachineDaoService virtualMachineDaoService;
    @Autowired
    private ProcessStepDaoService processStepDaoService;
    @Autowired
    private LeaseVMAndStartExecution leaseVMAndStartExecution;

    @Async//("serviceProcessExecuter")
    public void startInvocation(List<ProcessStep> processSteps) {

//        synchronized (ProcessInstancePlacementProblemServiceImpl.SYNCH_OBJECT) {
        final Map<VirtualMachine, List<ProcessStep>> tmp = new HashMap<>();
        for (final ProcessStep processStep : processSteps) {
//            ProcessStep processStep = clone(finalStep);

            processStep.setStartDate(new Date());
            VirtualMachine scheduledAt = processStep.getScheduledAtVM();
            List<ProcessStep> tmpSteps = new ArrayList<>();
            if (tmp.containsKey(scheduledAt)) {
                tmpSteps.addAll(tmp.get(scheduledAt));
            }
            tmpSteps.add(processStep);
            tmp.put(scheduledAt, tmpSteps);
            processStepDaoService.update(processStep);
        }
        Map<Future<String>, VirtualMachine> futuresMap = new HashMap<>();
        for (final VirtualMachine virtualMachine : tmp.keySet()) {

//            final VirtualMachine virtualMachine = (VirtualMachine) SerializationUtils.clone(finalMachine);

            final List<ProcessStep> processSteps1 = tmp.get(virtualMachine);
            if (!virtualMachine.isLeased()) {
                virtualMachine.setLeased(true);
                virtualMachine.setStartedAt(new Date());
                virtualMachineDaoService.update(virtualMachine);

                Future<String> processAddresses = leaseVMAndStartExecution.leaseVMAndStartExecution(virtualMachine, processSteps1);
                futuresMap.put(processAddresses, virtualMachine);

            } else {
                leaseVMAndStartExecution.startExecutions(tmp.get(virtualMachine), virtualMachine);
            }
        }
    }



}
