package at.ac.tuwien.infosys.viepep.connectors;


import at.ac.tuwien.infosys.viepep.connectors.impl.exceptions.*;
import at.ac.tuwien.infosys.viepep.database.entities.VirtualMachine;
import at.ac.tuwien.infosys.viepep.database.entities.docker.DockerContainer;
import com.spotify.docker.client.messages.ContainerInfo;

import java.util.List;
import java.util.Map;

/**

 */
public interface ViePEPDockerControllerService {

    void initialize();

    /**
     * @param running specified if only currently running container or all should be returned
     * @return a map of VMs and list of Docker containers
     */
    public Map<VirtualMachine, List<DockerContainer>> getDockersPerVM(boolean running);

    List<DockerContainer> getDockers(VirtualMachine virtualMachine) throws CouldNotGetDockerException;

    DockerContainer startDocker(VirtualMachine virtualMachine, DockerContainer dockerContainer) throws CouldNotStartDockerException;

    boolean stopDocker(VirtualMachine virtualMachine, DockerContainer dockerContainer) throws CouldNotStopDockerException;

    ContainerInfo getDockerInfo(VirtualMachine virtualMachine, DockerContainer dockerContainer) throws ContainerNotFoundException;

    DockerContainer resizeContainer(VirtualMachine virtualMachine, DockerContainer newDockerContainer) throws CouldResizeDockerException;
}
