package at.ac.tuwien.infosys.viepep;

import at.ac.tuwien.infosys.viepep.connectors.impl.ViePEPOpenstackClientServiceImpl;
import at.ac.tuwien.infosys.viepep.reasoning.ReasoningActivator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.Scanner;

@SpringBootApplication
public class ViePepApplication implements CommandLineRunner {

	@Autowired
	private ReasoningActivator reasoningActivator;
	@Autowired
	private ViePEPOpenstackClientServiceImpl viePEPOpenstackClientService;

	@Value("${simulate}")
	private boolean simulate;

	public static void main(String[] args) {
		SpringApplication.run(ViePepApplication.class, args);
	}

	public void run(String... args) {
		System.out.println("Starting ViePEP 4.0...\n");

		try {
//			start();
			Scanner scanner = new Scanner(System.in);

			boolean running = true;
			boolean started = false;
			String input = "";

			started = true;

			if(!simulate) {
				viePEPOpenstackClientService.init();
			}



			reasoningActivator.initialize();


			while (running) {
/*				System.out.println("-----------Enter 'start' to begin or 'stop' to end -------------");
				input = scanner.nextLine();
				while (!input.equalsIgnoreCase("start") && !input.equalsIgnoreCase("stop")) {
					input = scanner.nextLine();
				}
				switch (input) {
					case "start":
						if (!started) {
							started = true;
							reasoningActivator.initialize();
						}
						break;
					case "stop":
//						running = false;
						reasoningActivator.stop();
//						stop();
						break;
				}
*/
				Thread.sleep(1000 * 60 * 10);

			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("Could not start API");
			e.printStackTrace();
		} finally {
			System.out.println("Terminating....");
//			stop();
			System.exit(1);
		}
	}
}
