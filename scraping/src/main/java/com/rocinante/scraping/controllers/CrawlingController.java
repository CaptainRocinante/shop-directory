package com.rocinante.scraping.controllers;

import com.rocinante.common.api.dto.MerchantCrawlDto;
import com.rocinante.datastore.dao.MerchantDao;
import com.rocinante.datastore.dao.MerchantInferredCategoryDao;
import com.rocinante.datastore.entities.Merchant;
import com.rocinante.datastore.entities.MerchantInferredCategory;
import com.rocinante.scraping.service.async.AsyncCrawlingService;
import java.net.MalformedURLException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
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
  private final MerchantDao merchantDao;
  private final MerchantInferredCategoryDao merchantInferredCategoryDao;

  @PostMapping("/categories")
  public void crawlCategoriesForMerchant(@RequestBody MerchantCrawlDto merchantCrawlDto)
      throws MalformedURLException {
    final OffsetDateTime lastCrawlDateCutoff =
        Instant.now().atOffset(ZoneOffset.UTC).minusDays(merchantCrawlDto.getDays());
    final Optional<Merchant> merchantOpt =
        merchantDao.findByUuidAndLastCrawledAtBeforeOrNull(
            UUID.fromString(merchantCrawlDto.getUuid()), lastCrawlDateCutoff);
    if (merchantOpt.isPresent()) {
      log.info("Begin Category Crawl for merchant {}", merchantCrawlDto.getUuid());
      asyncCrawlingService.crawlAndSaveCategoriesForMerchant(merchantOpt.get().getUuid());
    } else {
      log.info(
          "No merchant to crawl found with uuid {} and cutoff {}",
          merchantCrawlDto.getUuid(),
          merchantCrawlDto.getDays());
    }
  }

  @PostMapping("/products")
  public void crawlProductsForMerchant(@RequestBody MerchantCrawlDto merchantCrawlDto)
      throws MalformedURLException {
    log.info("Begin Product Crawl for merchant {}", merchantCrawlDto.getUuid());

    final OffsetDateTime lastCrawlDateCutoff =
        Instant.now().atOffset(ZoneOffset.UTC).minusDays(merchantCrawlDto.getDays());

    final List<MerchantInferredCategory> merchantCategories =
        merchantInferredCategoryDao.findByMerchantUuidAndLastCrawledAtBeforeOrNull(
            UUID.fromString(merchantCrawlDto.getUuid()), lastCrawlDateCutoff);
    log.info("Categories to crawl count: {}", merchantCategories.size());

    for (final MerchantInferredCategory inferredCategory : merchantCategories) {
      log.info("Handling {} {}", inferredCategory.getName(), inferredCategory.getUrl());
      asyncCrawlingService.crawlAndSaveProductsForCategory(inferredCategory.getUuid());
    }
  }
}
