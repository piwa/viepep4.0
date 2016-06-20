package at.ac.tuwien.infosys.viepep;

import at.ac.tuwien.infosys.viepep.connectors.ViePEPDockerControllerService;
import at.ac.tuwien.infosys.viepep.connectors.ViePEPOpenstackClientService;
import at.ac.tuwien.infosys.viepep.database.entities.VMType;
import at.ac.tuwien.infosys.viepep.database.entities.VirtualMachine;
import at.ac.tuwien.infosys.viepep.database.entities.docker.DockerConfiguration;
import at.ac.tuwien.infosys.viepep.database.entities.docker.DockerContainer;
import at.ac.tuwien.infosys.viepep.database.entities.docker.DockerImage;
import com.spotify.docker.client.messages.ContainerInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ViePepApplication.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
@TestPropertySource(properties = {"simulation = true", "autostart = false"})
@ActiveProfiles("test")
public class ViePepApplicationTests {


	@Value("${local.server.port}")
	int port;

	@Autowired
	private ViePEPOpenstackClientService viePEPOpenstackClient;
	@Autowired
	private ViePEPDockerControllerService dockerControllerService;

	private static boolean setUpIsDone = false;

	@Before
	public void initOpenStackClient() {
		if (setUpIsDone) {
			return;
		}
		viePEPOpenstackClient.init();

		setUpIsDone = true;
	}

	@Test
	public void testStartNewVM_AddContainer_ResizeContainer_Terminate() throws Exception {
		//starting a new VM instance
		//exampleService can be ignored
		String testVMIp = viePEPOpenstackClient.startNewVM("testVM", VMType.DUAL_CORE.flavor(), "exampleService");
		assertThat(testVMIp, notNullValue());

		//starting a new docker container
		DockerImage dockerImage = new DockerImage("exampleApp", "bonomat", "nodejs-hello-world", 8090, 3000);
		//DockerImage dockerImage = new DockerImage("exampleApp", "bonomat", "viepep-backend-services", 8080, 8080);
		DockerConfiguration dockerConfiguration = DockerConfiguration.SINGLE_CORE;

		DockerContainer dockerContainer = new DockerContainer(dockerImage, dockerConfiguration);

		VirtualMachine virtualMachine = new VirtualMachine("dummyVM", VMType.DUAL_CORE);
		//  virtualMachine.setIpAddress("128.130.172.226"); //TODO change this manualy if run from outside of the cloud,
		// but make sure to assign this IP to the VM you just created

		dockerContainer = dockerControllerService.startDocker(virtualMachine, dockerContainer);

		//collect some docker information
		ContainerInfo dockerInfo = dockerControllerService.getDockerInfo(virtualMachine, dockerContainer);
		assertThat(dockerInfo, notNullValue());

		//change docker configuration
		dockerContainer.setContainerConfiguration(DockerConfiguration.DUAL_CORE);
		dockerContainer = dockerControllerService.resizeContainer(virtualMachine, dockerContainer);

		//stop docker
		boolean b = dockerControllerService.stopDocker(virtualMachine, dockerContainer);


	}

}
