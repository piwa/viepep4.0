package at.ac.tuwien.infosys.viepep.connectors;

import com.woorea.openstack.nova.model.Server;

import java.util.List;

/**
 * Created by Philipp Hoenisch on 5/7/14.
 */
public interface ViePEPOpenstackClientService {
    /**
     * @param instanceId to be terminated
     * if service is not found, the command is just ignored
     */
    boolean terminateInstanceByID(String instanceId);

    /**
     * @param localeAddress of a VM to be terminated
     * @return true if successful
     */
    boolean terminateInstanceByIP(String localeAddress);


    /**
     * @return a list of active server ips
     */
    List<String> getServerIpList();

    /**
     * starts a new VM
     *
     * @param name       of the new VM
     * @param flavorName the VM flavor
     * @param serviceName the service which should be deployed on default
     * @return the address of newly started VM
     */
    String startNewVM(String name, String flavorName, String serviceName);

    /**
     * @param ip IP
     * @return true if vm is running, otherwise false
     */
    boolean isVMRunning(String ip);

    String getImageId();

    String getVMIdByIP(String s);

    String getBPMSIp();

    List<Server> getServerList();

    int getFreeCPUs();

    void cleanUpVMsNotRegistered(List<String> registeredVMs);
}
