package at.ac.tuwien.infosys.viepep.database.services;

import at.ac.tuwien.infosys.viepep.database.entities.ProcessStep;
import at.ac.tuwien.infosys.viepep.database.entities.VirtualMachine;
import at.ac.tuwien.infosys.viepep.database.repositories.ProcessStepElementRepository;
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

    public List<ProcessStep> findByVM(VirtualMachine virtualMachine) {
        return processStepElementRepository.findByVM(virtualMachine.getId());
    }

}
