package com.rocinante.shops.controllers;

import com.rocinante.crawlers.category.CategoryCrawler;
import com.rocinante.crawlers.summary.SummaryCrawler;
import com.rocinante.shops.api.MerchantDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/crawl", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class CrawlingController {
  private final CategoryCrawler categoryCrawler;
  private final SummaryCrawler summaryCrawler;

  @PostMapping("/categories")
  @Async
  public void crawlMerchantCategories(@RequestBody MerchantDto merchantDto) {
    log.info("Begin Category Crawl");
  }
}
