package com.rocinante.shops;

import com.rocinante.configuration.ScrapingSpringConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication
@EnableAutoConfiguration
@EnableAsync
@Import(ScrapingSpringConfiguration.class)
public class ShopDirectoryApplication {
  public static final String ASYNC_TASK_EXECUTOR = "asyncTaskExecutor";

  public static void main(String[] args) {
    SpringApplication.run(ShopDirectoryApplication.class, args);
  }

  @Bean(name = ASYNC_TASK_EXECUTOR)
  public TaskExecutor executorAsync() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(10);
    // Capping max pool size to 1/10th size of the tomcat pool (200), each pool thread will
    // maintain a headless chromium instance, so need to be careful with max pool size
    executor.setMaxPoolSize(20);
    // Large queue capacity as these tasks will take time
    executor.setQueueCapacity(1000);
    executor.setThreadNamePrefix("async-");
    executor.initialize();
    return executor;
  }
}
