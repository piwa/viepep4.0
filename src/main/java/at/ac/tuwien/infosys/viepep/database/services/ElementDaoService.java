package at.ac.tuwien.infosys.viepep.database.services;

import at.ac.tuwien.infosys.viepep.database.entities.Element;
import at.ac.tuwien.infosys.viepep.database.repositories.ElementRepository;
import at.ac.tuwien.infosys.viepep.reasoning.optimisation.impl.PlacementHelperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by philippwaibel on 17/05/16.
 */
@Component
public class ElementDaoService {

    @Autowired
    private ElementRepository elementRepository;
    @Autowired
    private PlacementHelperImpl placementHelperImpl;

    public void update(Element element) {
        elementRepository.save(element);
        placementHelperImpl.getNextWorkflowInstances(true);
    }

}
