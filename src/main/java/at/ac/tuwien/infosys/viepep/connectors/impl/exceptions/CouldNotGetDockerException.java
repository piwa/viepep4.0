package at.ac.tuwien.infosys.viepep.connectors.impl.exceptions;

import com.spotify.docker.client.DockerException;

/**
 */
public class CouldNotGetDockerException extends Exception {
    public CouldNotGetDockerException(DockerException e) {
        super(e);
    }
}
