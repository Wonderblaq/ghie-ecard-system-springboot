package com.registrations.GhIE_ecard.services;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class AsyncConfig {
    @Bean(name = "taskExecutor")
    public Executor taskExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // number of threads to keep alive
        executor.setCorePoolSize(5);

        executor.setMaxPoolSize(10); // max number of threads that can be used
        executor.setQueueCapacity(30); // capacity of queue before maxPool size kicks in

        // a prefix to identify these threads in your IntelliJ logs
        executor.setThreadNamePrefix("GhIE-Task-");
        executor.initialize();
        return executor;


    }

}
