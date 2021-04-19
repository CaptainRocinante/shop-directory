package com.rocinante.shops;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableAutoConfiguration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {"com.rocinante.datastore.dao"})
@EntityScan(basePackages = {"com.rocinante.datastore.entities"})
public class ShopDirectoryApplication {
  public static void main(String[] args) {
    SpringApplication.run(ShopDirectoryApplication.class, args);
  }
}
