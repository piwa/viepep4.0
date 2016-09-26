package at.ac.tuwien.infosys.viepep.reasoning.optimisation.docker;

import at.ac.tuwien.infosys.viepep.database.entities.ProcessStep;
import at.ac.tuwien.infosys.viepep.database.entities.docker.DockerConfiguration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by philippwaibel on 25/09/2016.
 */
@Component
@Getter
@Setter
public class DockerOptimizationResult {

    private long tau_t_1;
    private Map<ProcessStep, DockerConfiguration> processStepDockerConfigMap = new HashMap<>();


}
