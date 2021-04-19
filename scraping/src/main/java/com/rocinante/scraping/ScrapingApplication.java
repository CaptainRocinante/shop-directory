package com.rocinante.scraping;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableAutoConfiguration
@EnableTransactionManagement
@EnableAsync
@ComponentScan(basePackages="com.rocinante.datastore.dao")
public class ScrapingApplication {
  public static final String ASYNC_TASK_EXECUTOR = "asyncTaskExecutor";

  @Bean(name = ASYNC_TASK_EXECUTOR)
  public TaskExecutor executorAsync(
      @Value("${async.pool.size}") int poolSize,
      @Value("${async.pool.queue.capacity}") int queueCapacity) {
    final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(poolSize);
    executor.setMaxPoolSize(poolSize);
    executor.setQueueCapacity(queueCapacity);
    executor.setThreadNamePrefix("async-");
    executor.initialize();
    return executor;
  }

  public static void main(String[] args) {
    SpringApplication.run(ScrapingApplication.class, args);
  }
}
