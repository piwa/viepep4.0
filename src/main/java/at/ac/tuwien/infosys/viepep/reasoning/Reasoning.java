package at.ac.tuwien.infosys.viepep.reasoning;

import org.springframework.scheduling.annotation.Async;

import java.util.Date;
import java.util.concurrent.Future;

/**
 * Created by philippwaibel on 22/09/2016.
 */
public interface Reasoning {
    @Async
    Future<Boolean> runReasoning(Date date) throws InterruptedException;

    void stop();
}
