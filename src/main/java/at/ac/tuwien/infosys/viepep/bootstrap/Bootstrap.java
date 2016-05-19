package at.ac.tuwien.infosys.viepep.bootstrap;

import lombok.extern.log4j.Log4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * Created by philippwaibel on 04/05/16.
 */
@Component
@Log4j
public class Bootstrap implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

    }

}
