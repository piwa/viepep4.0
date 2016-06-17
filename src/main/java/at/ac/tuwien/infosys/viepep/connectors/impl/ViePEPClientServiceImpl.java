package at.ac.tuwien.infosys.viepep.connectors.impl;


import at.ac.tuwien.infosys.viepep.connectors.ViePEPAwsClientService;
import at.ac.tuwien.infosys.viepep.connectors.ViePEPClientService;
import at.ac.tuwien.infosys.viepep.connectors.ViePEPOpenstackClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ViePEPClientServiceImpl implements ViePEPClientService {
    @Autowired
    private ViePEPOpenstackClientService openstackClientService;
    @Autowired
    private ViePEPAwsClientService awsClientService;

    //TODO implement 1-4 intern 5-8 extern, weil ich ansonsten probleme mit dem v_k namensmodell bekomme
    //--> das ganze wird anhand des flavortypes zurückgegeben

    @Override
    public void terminateInstanceByID(String instanceId) {
        openstackClientService.terminateInstanceByID(instanceId);
        awsClientService.terminateInstanceByID(instanceId);
    }

    @Override
    public boolean terminateInstanceByIP(String localeAddress) {
        boolean resultOpenstack = openstackClientService.terminateInstanceByIP(localeAddress);
        if (!resultOpenstack) {
            boolean result = awsClientService.terminateInstanceByIP(localeAddress);
            if(result) {
                log.info("AWS EC2 VM with the IP " + localeAddress + " terminated");
            }
            return result;
        } else {
            log.info("OpenStack VM with the IP " + localeAddress + " terminated");
            return true;
        }
    }

    @Override
    public List<String> getServerIpList() {
        List<String> result = new ArrayList<>();
        result.addAll(openstackClientService.getServerIpList());
        result.addAll(awsClientService.getServerIpList());
        return result;
    }

    @Override
    public String startNewVM(String name, String flavorName, String serviceName, String location) {
        if (location.equals("internal")) {
            return openstackClientService.startNewVM(name, flavorName, serviceName);
        } else if (location.equals("aws")) {
            return awsClientService.startNewVM(name, flavorName, serviceName, location);
        }
        return null;
    }

    @Override
    public boolean isVMRunning(String vmID) {
        if (!openstackClientService.isVMRunning(vmID)) {
            return awsClientService.isVMRunning(vmID);
        }
        return true;
    }
}
