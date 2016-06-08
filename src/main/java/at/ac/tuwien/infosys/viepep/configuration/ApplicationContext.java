package at.ac.tuwien.infosys.viepep.configuration;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
@EnableCaching
public class ApplicationContext  {

    @Bean
    @Primary
    public SimpleAsyncTaskExecutor simpleAsyncTaskExecutor() {
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor();
        executor.setConcurrencyLimit(200);
        return executor;
    }

    @Bean(name = "serviceProcessExecuter")
    public ThreadPoolTaskExecutor serviceProcessExecuter() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setMaxPoolSize(200);
//        executor.setCorePoolSize(150);
//        executor.setQueueCapacity(150);
        executor.setMaxPoolSize(150);
        executor.setCorePoolSize(100);
        executor.setQueueCapacity(50);
        executor.initialize();
        return executor;
    }
/*
    @Bean
    public CacheManager cacheManager() {

        SimpleCacheManager cacheManager = new SimpleCacheManager();

        List<Cache> cacheList = new ArrayList<>();
//        cacheList.add(new ConcurrentMapCache("WorkflowElementCache"));
        cacheList.add(new ConcurrentMapCache("VirtualMachineCache"));
//        cacheList.add(new ConcurrentMapCache("ProcessStepElementCache"));
        cacheList.add(new ConcurrentMapCache("ElementCache"));

        cacheManager.setCaches(cacheList);

        return cacheManager;
    }
    */
}
