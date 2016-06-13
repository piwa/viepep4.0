package at.ac.tuwien.infosys.viepep.database.services;

import at.ac.tuwien.infosys.viepep.database.entities.docker.DockerImage;
import at.ac.tuwien.infosys.viepep.database.repositories.DockerImageRepository;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by philippwaibel on 13/06/16.
 */
public class DockerImageDaoService {

    @Autowired
    private DockerImageRepository dockerImageRepository;

    public DockerImage save(DockerImage dockerImage) {
        return dockerImageRepository.save(dockerImage);
    }

}
