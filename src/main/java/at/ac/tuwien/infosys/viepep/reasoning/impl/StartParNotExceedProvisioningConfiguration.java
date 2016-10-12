package at.ac.tuwien.infosys.viepep.reasoning.impl;

import at.ac.tuwien.infosys.viepep.reasoning.ProcessOptimizationResults;
import at.ac.tuwien.infosys.viepep.reasoning.optimisation.ProcessInstancePlacementProblemService;
import at.ac.tuwien.infosys.viepep.reasoning.optimisation.impl.StartParNotExceedImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

/**
 * @author Gerta Sheganaku
 */
@Slf4j
@Configuration
@Profile("StartParNotExceed-docker")
@PropertySource(value = "application-docker.properties")
public class StartParNotExceedProvisioningConfiguration {
	
	@Bean
	public ProcessInstancePlacementProblemService initializeParameters() {
		log.info("Profile StartParNotExceed");
		return new StartParNotExceedImpl();
	}
	
	@Bean
	public ProcessOptimizationResults processResults() {
		log.info("Profile StartParNotExceed");
		return new SimpleDockerProcessOptimizationResults();
	}

}
