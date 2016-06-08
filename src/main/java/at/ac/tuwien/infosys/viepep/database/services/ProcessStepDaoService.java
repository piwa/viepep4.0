package at.ac.tuwien.infosys.viepep.database.services;

import at.ac.tuwien.infosys.viepep.database.entities.ProcessStep;
import at.ac.tuwien.infosys.viepep.database.entities.VirtualMachine;
import at.ac.tuwien.infosys.viepep.database.repositories.ProcessStepElementRepository;
import at.ac.tuwien.infosys.viepep.reasoning.optimisation.PlacementHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by philippwaibel on 17/05/16.
 */
@Component
@Slf4j
public class ProcessStepDaoService {

    @Autowired
    private ProcessStepElementRepository processStepElementRepository;
    @Autowired
    private PlacementHelper placementHelperImpl;

    public void update(ProcessStep processStep) {
//            log.info("Save processStep: " + processStep.toString());
            processStepElementRepository.save(processStep);
            placementHelperImpl.getNextWorkflowInstances(true);
    }

    public List<ProcessStep> getUnfinishedSteps() {
        return processStepElementRepository.getUnfinishedSteps();
    }

    public List<ProcessStep> findByVM(VirtualMachine virtualMachine) {
        return processStepElementRepository.findByVM(virtualMachine.getId());
    }

}
