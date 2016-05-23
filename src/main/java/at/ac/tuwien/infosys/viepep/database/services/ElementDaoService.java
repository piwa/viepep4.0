package at.ac.tuwien.infosys.viepep.database.services;

import at.ac.tuwien.infosys.viepep.database.entities.Element;
import at.ac.tuwien.infosys.viepep.database.repositories.ElementRepository;
import at.ac.tuwien.infosys.viepep.reasoning.optimisation.impl.PlacementHelperImpl;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by philippwaibel on 17/05/16.
 */
@Component
@Log4j
public class ElementDaoService {

    @Autowired
    private ElementRepository elementRepository;
    @Autowired
    private PlacementHelperImpl placementHelperImpl;

    public void update(Element element) {
        log.info("Save element: " + element.toString());
        elementRepository.save(element);
        placementHelperImpl.getNextWorkflowInstances(true);
    }

}
