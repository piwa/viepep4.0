package at.ac.tuwien.infosys.viepep.reasoning;

import at.ac.tuwien.infosys.viepep.database.entities.VMType;
import at.ac.tuwien.infosys.viepep.database.entities.VirtualMachine;
import at.ac.tuwien.infosys.viepep.database.entities.WorkflowElement;
import at.ac.tuwien.infosys.viepep.database.services.VirtualMachineDaoService;
import at.ac.tuwien.infosys.viepep.database.services.WorkflowDaoService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by philippwaibel on 17/05/16.
 */
@Component
@Scope("prototype")
@Log4j
public class ReasoningActivator {

    @Autowired
    private WorkflowDaoService workflowDaoService;
    @Autowired
    private VirtualMachineDaoService virtualMachineDaoService;
    @Autowired
    private Reasoning reasoning;

    private List<WorkflowElement> workflows;

    private int V = 6; //Available types of VM's
    private int K = 2;


    public void initialize() throws Exception {
        log.info("ReasoningActivator initialized");

        workflows = new ArrayList<>();

        workflows = workflowDaoService.getAllWorkflowElementsList();

        virtualMachineDaoService.removeAllVms();
        List<VirtualMachine> vms = new ArrayList<>();
        try {
            for (int v = 0; v < V; v++) {
                VMType type = VMType.fromIdentifier(v + 1);
                for (int k = 0; k < K; k++) {
                    vms.add(new VirtualMachine(v + "_" + k, type));
                }
            }
        } catch (Exception ex) {
            log.error("EXCEPTION", ex);
        }

        virtualMachineDaoService.saveVms(vms);
        reasoning.runReasoning(new Date());
    }
}
