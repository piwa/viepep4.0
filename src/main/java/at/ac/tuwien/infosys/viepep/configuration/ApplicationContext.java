package at.ac.tuwien.infosys.viepep.configuration;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Created by philippwaibel on 03/05/16.
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan(value="at.ac.tuwien.infosys.viepep")
@EnableRetry
@EnableScheduling
@EnableAsync
public class ApplicationContext  {

    @Bean
    public SimpleAsyncTaskExecutor simpleAsyncTaskExecutor() {
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor();
        executor.setConcurrencyLimit(100);
        return executor;
    }

    @Bean(name = "serviceProcessExecuter")
    public ThreadPoolTaskExecutor serviceProcessExecuter() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(100);
        executor.setCorePoolSize(50);
        executor.setQueueCapacity(100);
        return executor;
    }
}
