package at.ac.tuwien.infosys.viepep.database.services;

import at.ac.tuwien.infosys.viepep.database.entities.docker.DockerContainer;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by philippwaibel on 13/06/16.
 */
public class DockerContainerService {

    @Autowired
    private DockerContainerService dockerContainerService;

    public DockerContainer save(DockerContainer dockerContainer) {
        return dockerContainerService.save(dockerContainer);
    }

}
