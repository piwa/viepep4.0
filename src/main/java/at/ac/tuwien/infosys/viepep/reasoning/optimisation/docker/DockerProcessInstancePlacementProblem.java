package at.ac.tuwien.infosys.viepep.reasoning.optimisation.docker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by philippwaibel on 25/09/2016.
 */
@Component
public class DockerProcessInstancePlacementProblem {

    @Autowired
    private SimpleDockerOptimization simpleDockerOptimization;


    public DockerOptimizationResult optimize(Date tau_t_0) {

        return simpleDockerOptimization.optimize(tau_t_0);


    }


}
