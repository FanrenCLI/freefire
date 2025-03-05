package com.fanrencli.freefire.configurator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@ComponentScan(basePackages = {
        "com.fanrencli.freefire"
})
@EnableScheduling
@EnableAsync
public class FreeFireConfiguration {

    @Bean(name = "genUnitTestFileThreadPool")
    public ThreadPoolTaskExecutor genUnitTestFileThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("genUnitTestFileThreadPool-");
        executor.initialize();
        return executor;
    }
}
