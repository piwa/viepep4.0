package at.ac.tuwien.infosys.viepep;

import at.ac.tuwien.infosys.viepep.connectors.ViePEPAwsClientService;
import at.ac.tuwien.infosys.viepep.connectors.impl.ViePEPOpenstackClientServiceImpl;
import at.ac.tuwien.infosys.viepep.reasoning.ReasoningActivator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;
import java.util.concurrent.Future;

@SpringBootApplication
@Slf4j
public class ViePepApplication implements CommandLineRunner {

	@Autowired
	private ReasoningActivator reasoningActivatorImpl;
	@Autowired
	private ViePEPOpenstackClientServiceImpl viePEPOpenstackClientService;
	@Autowired
	private ViePEPAwsClientService viePEPAwsClientService;

	@Value("${simulate}")
	private boolean simulate;
	@Value("${autostart}")
	private boolean autostart;

	public static void main(String[] args) {
		SpringApplication.run(ViePepApplication.class, args);
	}

	public void run(String... args) {
		log.info("Starting ViePEP 4.0...");

		try {
			Scanner scanner = new Scanner(System.in);

			boolean running = true;
			boolean started = false;
			String input = "";

			started = true;

			if(!simulate) {
				viePEPOpenstackClientService.init();
				viePEPAwsClientService.init();
			}

			reasoningActivatorImpl.initialize();

			if(autostart) {
				Future<Boolean> reasoningDone = reasoningActivatorImpl.start();
				while(!reasoningDone.isDone()) {
					Thread.sleep(10000);
				}
			}
			else {
				while (running) {
					log.info("-----------Enter 'start' to begin or 'stop' to end -------------");
					input = scanner.nextLine();
					while (!input.equalsIgnoreCase("start") && !input.equalsIgnoreCase("stop")) {
						input = scanner.nextLine();
					}
					switch (input) {
						case "start":
							if (!started) {
								started = true;
								reasoningActivatorImpl.start();
							}
							break;
						case "stop":
							running = false;
							reasoningActivatorImpl.stop();
							break;
					}
				}
			}

		} catch (Exception e) {
			log.error("EXCEPTION", e);
		} finally {
			log.info("Terminating....");
			System.exit(1);
		}
	}
}
