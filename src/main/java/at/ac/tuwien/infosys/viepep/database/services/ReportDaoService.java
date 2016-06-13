package at.ac.tuwien.infosys.viepep.database.services;

import at.ac.tuwien.infosys.viepep.database.entities.ReportingAction;
import at.ac.tuwien.infosys.viepep.database.repositories.ReportRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by philippwaibel on 17/05/16.
 */
@Component
@Slf4j
public class ReportDaoService {

    @Autowired
    private ReportRepository reportRepository;

    public void save(ReportingAction reportingAction) {
        reportRepository.save(reportingAction);
    }

}
