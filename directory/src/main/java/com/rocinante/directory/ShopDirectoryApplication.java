package com.rocinante.directory;

import com.rocinante.configuration.ScrapingSpringConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(ScrapingSpringConfiguration.class)
public class ShopDirectoryApplication {
  public static void main(String[] args) {
    SpringApplication.run(ShopDirectoryApplication.class, args);
  }
}
