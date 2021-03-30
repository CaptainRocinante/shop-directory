package com.rocinante.shops.controllers;

import com.rocinante.crawlers.category.CategoryCrawler;
import com.rocinante.crawlers.summary.SummaryCrawler;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class CrawlingController {
  private final CategoryCrawler categoryCrawler;
  private final SummaryCrawler summaryCrawler;

  @RequestMapping("/")
  public String index() {
    return "Greetings from Spring Boot!";
  }
}
