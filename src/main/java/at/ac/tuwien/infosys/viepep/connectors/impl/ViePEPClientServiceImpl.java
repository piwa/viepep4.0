package at.ac.tuwien.infosys.viepep.connectors.impl;


import at.ac.tuwien.infosys.viepep.connectors.ViePEPAwsClientService;
import at.ac.tuwien.infosys.viepep.connectors.ViePEPClientService;
import at.ac.tuwien.infosys.viepep.connectors.ViePEPOpenstackClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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
        if (!openstackClientService.terminateInstanceByIP(localeAddress)) {
            return awsClientService.terminateInstanceByIP(localeAddress);
        } else {
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
        if ((flavorName.substring(0, 2).equals("m1")) || (flavorName.substring(0, 2).equals("m2"))) {
            return openstackClientService.startNewVM(name, flavorName, serviceName);
        } else {
            if (flavorName.substring(0, 2).equals("m3")) {
                return awsClientService.startNewVM(name, flavorName, serviceName, location);
            }
        }
        return null;
    }

    @Override
    public String startNewVM(String name, String flavorName, String serviceName) {
        return startNewVM(name, flavorName, serviceName, "");
    }

    @Override
    public boolean isVMRunning(String vmID) {
        if (!openstackClientService.isVMRunning(vmID)) {
            return awsClientService.isVMRunning(vmID);
        }
        return true;
    }
}
