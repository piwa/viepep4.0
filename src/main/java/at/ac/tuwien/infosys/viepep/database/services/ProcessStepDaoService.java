package at.ac.tuwien.infosys.viepep.database.services;

import at.ac.tuwien.infosys.viepep.database.entities.ProcessStep;
import at.ac.tuwien.infosys.viepep.database.entities.ServiceType;
import at.ac.tuwien.infosys.viepep.database.entities.VirtualMachine;
import at.ac.tuwien.infosys.viepep.database.repositories.ProcessStepElementRepository;
import at.ac.tuwien.infosys.viepep.reasoning.optimisation.impl.PlacementHelperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by philippwaibel on 17/05/16.
 */
@Component
public class ProcessStepDaoService {

    @Autowired
    private ProcessStepElementRepository processStepElementRepository;
    @Autowired
    private PlacementHelperImpl placementHelperImpl;

    public void update(ProcessStep processStep) {
        processStepElementRepository.save(processStep);
        placementHelperImpl.getNextWorkflowInstances(true);
    }

    public List<ServiceType> getProcessStepTypes() {
        return processStepElementRepository.getProcessStepTypes();
    }

    public List<ProcessStep> getUnfinishedSteps() {
        return processStepElementRepository.getUnfinishedSteps();
    }

    public ProcessStep find(ProcessStep finalStep) {
        return processStepElementRepository.find(finalStep.getId());
    }

    public List<ProcessStep> findByVM(VirtualMachine virtualMachine) {
        return processStepElementRepository.findByVM(virtualMachine.getId());
    }

}
