package io.github.roony11_1.async_laboratory.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync 
public class ThreadConfig 
{
    @Bean
    public TaskExecutor jobExecutor()
    {
        var exe = new ThreadPoolTaskExecutor();

        exe.setCorePoolSize(2);
        exe.setMaxPoolSize(4);
        exe.setQueueCapacity(10);
        exe.setKeepAliveSeconds(60);
        exe.setThreadNamePrefix("job-executor");

        return exe;
    }
}