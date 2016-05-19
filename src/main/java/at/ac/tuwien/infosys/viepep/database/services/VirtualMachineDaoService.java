package at.ac.tuwien.infosys.viepep.database.services;

import at.ac.tuwien.infosys.viepep.database.entities.VirtualMachine;
import at.ac.tuwien.infosys.viepep.database.repositories.VirtualMachineRepository;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by philippwaibel on 17/05/16.
 */
@Component
public class VirtualMachineDaoService {

    @Autowired
    private VirtualMachineRepository virtualMachineRepository;

    private List<VirtualMachine> vms;

    public void removeAllVms() {
        virtualMachineRepository.deleteAll();
    }

    public Iterable<VirtualMachine> saveVms(List<VirtualMachine> vms) {
        return virtualMachineRepository.save(vms);
    }

    public List<VirtualMachine> getAllVms() {
        return Lists.newArrayList(virtualMachineRepository.findAll().iterator());
    }

    public void updateVM(VirtualMachine virtualMachine) {
        virtualMachineRepository.save(virtualMachine);
    }

    public VirtualMachine update(VirtualMachine virtualMachine) {
        return virtualMachineRepository.save(virtualMachine);
    }
}
