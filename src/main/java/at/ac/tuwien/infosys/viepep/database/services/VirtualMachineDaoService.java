package at.ac.tuwien.infosys.viepep.database.services;

import at.ac.tuwien.infosys.viepep.database.entities.VirtualMachine;
import at.ac.tuwien.infosys.viepep.database.repositories.VirtualMachineRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by philippwaibel on 17/05/16.
 */
@Component
@Slf4j
public class VirtualMachineDaoService {

    @Autowired
    private VirtualMachineRepository virtualMachineRepository;

    public VirtualMachine update(VirtualMachine virtualMachine) {
        return virtualMachineRepository.save(virtualMachine);
    }

    public VirtualMachine getVm(VirtualMachine vm) {
        return virtualMachineRepository.findOne(vm.getId());
    }
}
