package at.ac.tuwien.infosys.viepep.reasoning;

import at.ac.tuwien.infosys.viepep.database.entities.Element;
import at.ac.tuwien.infosys.viepep.database.entities.ProcessStep;
import at.ac.tuwien.infosys.viepep.database.entities.VirtualMachine;
import at.ac.tuwien.infosys.viepep.database.entities.WorkflowElement;
import at.ac.tuwien.infosys.viepep.database.services.VirtualMachineDaoService;
import at.ac.tuwien.infosys.viepep.database.services.WorkflowDaoService;
import at.ac.tuwien.infosys.viepep.reasoning.optimisation.PlacementHelper;
import at.ac.tuwien.infosys.viepep.reasoning.optimisation.impl.ProcessInstancePlacementProblemServiceImpl;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import net.sf.javailp.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by philippwaibel on 18/05/16.
 */
@Log4j
@Component
@Scope("prototype")
@Setter
public class ProcessResults {//implements Runnable {

    @Autowired
    private PlacementHelper placementHelper;
    @Autowired
    private WorkflowDaoService workflowDaoService;
    @Autowired
    private VirtualMachineDaoService virtualMachineDaoService;
    @Autowired
    private ProcessInvocation processInvocation;

    private Result optimize;
    private Date tau_t;

    @Async
    public void processResults(Result optimize, Date tau_t) {
        this.optimize = optimize;
        this.tau_t = tau_t;
/*
        Thread thread = new Thread(this);
        thread.start();
    }

    public void run() {
*/        //start VMs
        List<VirtualMachine> vmsToStart = new ArrayList<>();
        //set steps to be scheduled
        List<ProcessStep> scheduledForExecution = new ArrayList<>();
        List<String> y = new ArrayList<>();
        StringBuilder stringBuilder2 = new StringBuilder();
//        synchronized (this) {
        List<WorkflowElement> allWorkflowInstances = workflowDaoService.getAllWorkflowInstances();


        stringBuilder2.append("-------------Tasks running -------------------\n");
        List<VirtualMachine> vMs = placementHelper.getVMs(true);
        List<Element> nextSteps = workflowDaoService.getUnfinishedSteps();//placementHelper.getUnfinishedSteps();
        for (Element workflow : allWorkflowInstances) {
            List<Element> runningSteps = placementHelper.getRunningProcessSteps(workflow.getName());
            for (Element runningStep : runningSteps) {
                stringBuilder2.append("Task-Running: ").append(runningStep).append("\n");
            }

            for (Element element : nextSteps) {
                if (!((ProcessStep) element).getWorkflowName().equals(workflow.getName())) {
                    continue;
                }
                //check if step has to be started
                for (VirtualMachine virtualMachine : vMs) {
                    String x_v_k = "x_" + element.getName() + "," + virtualMachine.getName();
                    String y_v_k = "y_" + virtualMachine.getName();

                    Number x_v_k_number = optimize.get(x_v_k);
                    Number y_v_k_number = optimize.get(y_v_k);

                    if (!y.contains(y_v_k)) {
                        y.add(y_v_k);
                        if (y_v_k_number.intValue() >= 1) {
                            vmsToStart.add(virtualMachine);
                            Date date = new Date();
                            if (virtualMachine.getToBeTerminatedAt() != null) {
                                date = virtualMachine.getToBeTerminatedAt();
                            }
                            virtualMachine.setToBeTerminatedAt(new Date(date.getTime() + (ProcessInstancePlacementProblemServiceImpl.LEASING_DURATION * y_v_k_number.intValue())));
                            virtualMachineDaoService.updateVM(virtualMachine);

                        }
                    }

                    if (x_v_k_number == null || x_v_k_number.intValue() == 0) {
                        continue;
                    }


                    ProcessStep processStep = (ProcessStep) element;
                    if (x_v_k_number.intValue() == 1 && !scheduledForExecution.contains(processStep) &&
                            processStep.getStartDate() == null) {
                        processStep.setScheduledForExecution(true, tau_t);
                        processStep.setScheduledAtVM(virtualMachine);
                        scheduledForExecution.add(processStep);
                        virtualMachine.setServiceType(processStep.getServiceType());
//                        virtualMachine.addAssignedSteps(processStep);
                        if (!vmsToStart.contains(virtualMachine)) {
                            vmsToStart.add(virtualMachine);
                        }
                    }
                }
            }
//            }
        }
        stringBuilder2.append("--------------y results-----------------------\n");
        for (String s : y) {
            stringBuilder2.append(s).append("=").append(optimize.get(s)).append("\n");
        }
        stringBuilder2.append("----------VM should be started or running:----\n");
        for (VirtualMachine virtualMachine : vmsToStart) {
            stringBuilder2.append(virtualMachine).append("\n");
        }

        stringBuilder2.append("-------------Tasks to be started -------------\n");


        for (ProcessStep processStep : scheduledForExecution) {
            stringBuilder2.append("Task-TODO: ").append(processStep).append("\n");
        }
        stringBuilder2.append("----------------------------------------------\n");
        log.info(stringBuilder2.toString().replaceAll("(\r\n|\n)", "\r\n                                               "));


        processInvocation.startInvocation(scheduledForExecution);

        cleanupVMs(tau_t);
    }

    private void cleanupVMs(Date tau_t_0) {
        List<VirtualMachine> vMs = placementHelper.getVMs(true);
        for (VirtualMachine vM : vMs) {
            if (vM.getToBeTerminatedAt() != null && vM.getToBeTerminatedAt().before((tau_t_0))) {
                placementHelper.terminateVM(vM);
            }
        }
    }



}
