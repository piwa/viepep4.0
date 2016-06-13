package at.ac.tuwien.infosys.viepep;

import at.ac.tuwien.infosys.viepep.database.entities.ProcessStep;
import at.ac.tuwien.infosys.viepep.database.entities.Sequence;
import at.ac.tuwien.infosys.viepep.database.entities.ServiceType;
import at.ac.tuwien.infosys.viepep.database.entities.WorkflowElement;
import at.ac.tuwien.infosys.viepep.database.repositories.WorkflowElementRepository;
import at.ac.tuwien.infosys.viepep.database.services.WorkflowDaoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Date;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ViePepApplication.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
public class ViePepApplicationTests {

	@Autowired
	private WorkflowDaoService workflowDaoService;
	@Autowired
	private WorkflowElementRepository workflowElementRepository;

	@Value("${local.server.port}")
	int port;

	@Test
	public void persistWorkflow_ShouldPersistWorkflow() {

		WorkflowElement workflowElement = createFinishedWorkflow();
		workflowElement.setFinishedAt(new Date());
		workflowElement = workflowDaoService.finishWorkflow(workflowElement);
		WorkflowElement workflowFromDatabase = workflowElementRepository.findOne(workflowElement.getId());
		assertNotNull(workflowFromDatabase);
	}



	private static WorkflowElement createFinishedWorkflow() {
		String name = "finishedWorkflow";
		WorkflowElement workflow = new WorkflowElement(name, (new Date()).getTime() + 10000);
		Sequence seq = new Sequence(name + "-seq");
		ProcessStep elem1 = new ProcessStep(name + ".1", ServiceType.Task1, workflow.getName());
		seq.addElement(elem1);
		ProcessStep elem2 = new ProcessStep(name + ".2", ServiceType.Task2, workflow.getName());
		seq.addElement(elem2);
		ProcessStep elem = new ProcessStep(name + ".3", ServiceType.Task3, workflow.getName());
		elem.setLastElement(true);
		elem.setFinishedAt(new Date());
		seq.addElement(elem);
		workflow.addElement(seq);

		return workflow;
	}

}
