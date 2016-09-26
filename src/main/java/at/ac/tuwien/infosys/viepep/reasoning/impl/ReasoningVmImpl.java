package at.ac.tuwien.infosys.viepep.reasoning.impl;

import at.ac.tuwien.infosys.viepep.database.entities.ProcessStep;
import at.ac.tuwien.infosys.viepep.database.entities.WorkflowElement;
import at.ac.tuwien.infosys.viepep.database.inmemory.services.CacheWorkflowService;
import at.ac.tuwien.infosys.viepep.database.services.WorkflowDaoService;
import at.ac.tuwien.infosys.viepep.reasoning.Reasoning;
import at.ac.tuwien.infosys.viepep.reasoning.optimisation.general.PlacementHelper;
import at.ac.tuwien.infosys.viepep.reasoning.optimisation.vm.ProcessInstancePlacementProblemService;
import lombok.extern.slf4j.Slf4j;
import net.sf.javailp.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by philippwaibel on 17/05/16.
 */
@Component
@Scope("prototype")
@Slf4j
public class ReasoningVmImpl implements Reasoning {

    @Autowired
    private ProcessOptimizationResults processOptimizationResults;
    @Autowired
    private ProcessInstancePlacementProblemService resourcePredictionService;
    @Autowired
    private PlacementHelper placementHelper;
    @Autowired
    private CacheWorkflowService cacheWorkflowService;
    @Autowired
    private WorkflowDaoService workflowDaoService;

    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private Date tau_t;
    private boolean run = true;


    @Override@Async
    public Future<Boolean> runReasoning(Date date) throws InterruptedException {
        tau_t = date;

        resourcePredictionService.initializeParameters();
        run = true;

        Result optimize = null;
        long tau_t_0_time = tau_t.getTime();
        Date tau_t_0 = tau_t;
        int count = 0;
        int emptyCounter = 0;


        while (run) {
            synchronized (this) {
                try {
                    long difference = performOptimisation(tau_t_0_time, tau_t_0);

                    Thread.sleep(difference);

                    //finishWorkflow tau t for next round
                    tau_t_0 = new Date();
                    tau_t_0_time = tau_t_0.getTime();
                    boolean empty = cacheWorkflowService.getRunningWorkflowInstances().isEmpty();
                    if(empty) {
                        emptyCounter++;
                    }
                    else {
                        emptyCounter = 0;
                    }
                    if ((count >= 150 && empty) || emptyCounter > 4) {
                        run = false;
                    }
                    count++;
                } catch (Exception ex) {
                    log.error("An exception occurred, exit, check if tau was always divided by 1000 and or multiplied afterwards :D !!!!:\n", ex);
                    run = false;
                }
            }
        }

        waitUntilAllProcessDone();

        List<WorkflowElement> workflows = cacheWorkflowService.getAllWorkflowElements();
        int delayed = 0;
        for (WorkflowElement workflow : workflows) {
            log.info("workflow: " + workflow.getName() + " Deadline: " + formatter.format(new Date(workflow.getDeadline())));

            ProcessStep lastExecutedElement = workflow.getLastExecutedElement();
            if (lastExecutedElement != null) {
                Date finishedAt = lastExecutedElement.getFinishedAt();
                workflow.setFinishedAt(finishedAt);
                boolean ok = workflow.getDeadline() >= finishedAt.getTime();
                long delay = finishedAt.getTime() - workflow.getDeadline();
                String message = " LastDone: " + formatter.format(finishedAt);
                if (ok) {
                    log.info(message + " : was ok");
                } else {
                    log.info(message + " : delayed in seconds: " + delay / 1000);
                    delayed++;
                }
                cacheWorkflowService.deleteRunningWorkflowInstance(workflow);
            } else {
                log.info(" LastDone: not yet finished");
            }
        }
        log.info(String.format("From %s workflows, %s where delayed", workflows.size(), delayed));


        for(WorkflowElement workflowElement : cacheWorkflowService.getAllWorkflowElements()) {
            workflowDaoService.finishWorkflow(workflowElement);
        }

        return new AsyncResult<>(true);
    }

    private void waitUntilAllProcessDone() {
        int times = 0;
        int size = placementHelper.getRunningSteps(true).size();
        while (size != 0 && times <= 5) {
            log.info("there are still steps running waiting 1 minute: steps running: " + size);
            try {
                Thread.sleep(60000);//
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            size = placementHelper.getRunningSteps(true).size();
            times++;
        }
    }

    private long performOptimisation(long tau_t_0_time, Date tau_t_0) throws Exception {

        log.info("---------tau_t_0 : " + tau_t_0 + " ------------------------");
        log.info("---------tau_t_0.time : " + tau_t_0_time + " ------------------------");
        tau_t_0 = new Date();
        Result optimize = resourcePredictionService.optimize(tau_t_0);          // TODO call the optimization also at specific events (e.g. a new workflow is added)

        if (optimize == null) {
            throw new Exception("Could not solve the Problem");
        }

        log.info("Objective: " + optimize.getObjective());
        long tau_t_1 = optimize.get("tau_t_1").longValue() * 1000;//VERY IMPORTANT,
        long difference = tau_t_1 - new Date().getTime();
        if (difference < 0) {
            difference = 0;
        }
        log.info("---------sleep for: " + difference / 1000 + " seconds-----------");
        log.info("---------next iteration: " + new Date(tau_t_1) + " -----------");

        final Result finalOptimize = optimize;
        final Date finalTau_t_ = tau_t_0;



        processOptimizationResults.processResults(finalOptimize, finalTau_t_);

        return difference;
    }

    @Override public void stop() {
        this.run = false;
    }
}
