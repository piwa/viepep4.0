package at.ac.tuwien.infosys.viepep.reasoning.impl;

import at.ac.tuwien.infosys.viepep.reasoning.ProcessOptimizationResults;
import at.ac.tuwien.infosys.viepep.reasoning.optimisation.ProcessInstancePlacementProblemService;
import at.ac.tuwien.infosys.viepep.reasoning.optimisation.impl.SimpleDockerProcessInstancePlacementProblemServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

/**
 * @author Gerta Sheganaku
 */

@Configuration
@Profile("simple-docker")
@PropertySource(value = "application-docker.properties")
public class SimpleDockerOptimizerConfiguration {
	
	@Bean
	public ProcessInstancePlacementProblemService initializeParameters() {
		System.out.println("Profile docker!!");
		return new SimpleDockerProcessInstancePlacementProblemServiceImpl();
	}
	
	@Bean
	public ProcessOptimizationResults processResults() {
		System.out.println("Profile docker!!");
		return new SimpleDockerProcessOptimizationResults();
	}

}
