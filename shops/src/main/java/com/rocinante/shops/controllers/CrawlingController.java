package com.rocinante.shops.controllers;

import com.rocinante.shops.api.MerchantDto;
import com.rocinante.shops.service.AsyncCrawlingService;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/crawl", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class CrawlingController {
  private final AsyncCrawlingService asyncCrawlingService;

  @PostMapping("/categories")
  public void crawlCategories(@RequestBody MerchantDto merchantDto) {
    asyncCrawlingService.crawlAndSaveCategoriesForMerchant(UUID.fromString(merchantDto.getUuid()));
  }
}
