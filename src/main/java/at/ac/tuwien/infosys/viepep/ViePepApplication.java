package at.ac.tuwien.infosys.viepep;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class ViePepApplication {

	public static void main(String[] args) {
		SpringApplication.run(ViePepApplication.class, args);
	}
}