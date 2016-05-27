package at.ac.tuwien.infosys.viepep.reasoning;

import at.ac.tuwien.infosys.viepep.database.entities.ProcessStep;
import at.ac.tuwien.infosys.viepep.database.entities.WorkflowElement;
import at.ac.tuwien.infosys.viepep.database.services.WorkflowDaoService;
import at.ac.tuwien.infosys.viepep.reasoning.optimisation.PlacementHelper;
import at.ac.tuwien.infosys.viepep.reasoning.optimisation.ProcessInstancePlacementProblemService;
import lombok.extern.slf4j.Slf4j;
import net.sf.javailp.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by philippwaibel on 17/05/16.
 */
@Component
@Scope("prototype")
@Slf4j
public class Reasoning {//implements Runnable {

    @Autowired
    private ProcessResults processResults;
    @Autowired
    private ProcessInstancePlacementProblemService resourcePredictionService;
    @Autowired
    private PlacementHelper placementHelper;
    @Autowired
    private WorkflowDaoService workflowDaoService;

    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private Date tau_t;
    private boolean run = true;


    @Async
    public void runReasoning(Date date) throws InterruptedException {
        tau_t = date;
/*        Thread thread = new Thread(this);
        thread.start();
    }

    public void run() {
*/        resourcePredictionService.initializeParameters();
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

                    //update tau t for next round
                    tau_t_0 = new Date();
                    tau_t_0_time = tau_t_0.getTime();
                    boolean empty = placementHelper.getNextWorkflowInstances(true).isEmpty();
                    if(empty) {
                        emptyCounter++;
                    }
                    else {
                        emptyCounter = 0;
                    }
//                    if (count >= 30 && empty) {
                    if ((count >= 100 && empty) || emptyCounter > 3) {
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

        List<WorkflowElement> workflows = workflowDaoService.getAllWorkflowElementsList();
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
                workflowDaoService.update(workflow);
            } else {
                log.info(" LastDone: not yet finished");
            }
        }
        log.info(String.format("From %s workflows, %s where delayed", workflows.size(), delayed));

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
        Result optimize = resourcePredictionService.optimize(tau_t_0);

        if (optimize == null) {
            throw new Exception("Could not solve the Problem");
        }

        log.info("Objective: " + optimize.getObjective());
        long tau_t_1 = optimize.get("tau_t_1").longValue() * 1000;//VERY IMPORTANT,
        long difference = tau_t_1 - new Date().getTime();
        log.info("---------sleep for: " + difference / 1000 + " seconds-----------");
        log.info("---------next iteration: " + new Date(tau_t_1) + " -----------");
        if (difference < 0) {
            difference = 0;
        }
        final Result finalOptimize = optimize;
        final Date finalTau_t_ = tau_t_0;

        processResults.processResults(finalOptimize, finalTau_t_);

        return difference;
    }
}
