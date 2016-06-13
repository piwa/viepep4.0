package at.ac.tuwien.infosys.viepep.database.repositories;

import at.ac.tuwien.infosys.viepep.database.entities.docker.DockerImage;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by philippwaibel on 13/06/16.
 */
public interface DockerImageRepository extends CrudRepository<DockerImage, Long> {
}
