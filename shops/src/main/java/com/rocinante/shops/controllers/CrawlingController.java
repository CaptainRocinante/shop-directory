package com.rocinante.shops.controllers;

import com.rocinante.crawlers.MapCrawlContext;
import com.rocinante.crawlers.category.Category;
import com.rocinante.crawlers.category.CategoryCrawler;
import com.rocinante.crawlers.summary.SummaryCrawler;
import com.rocinante.shops.api.MerchantDto;
import com.rocinante.shops.datastore.dao.MerchantDao;
import com.rocinante.shops.datastore.dao.MerchantInferredCategoryDao;
import com.rocinante.shops.datastore.entities.Merchant;
import com.rocinante.shops.datastore.entities.MerchantInferredCategory;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
  private final MerchantDao merchantDao;
  private final MerchantInferredCategoryDao merchantInferredCategoryDao;
  private final CategoryCrawler categoryCrawler;
  private final SummaryCrawler summaryCrawler;

  @PostMapping("/categories")
  @Async
  public void crawlMerchantCategories(@RequestBody MerchantDto merchantDto) {
    log.info("Begin Category Crawl for merchant {}", merchantDto.getUuid());
    final Merchant merchant = merchantDao
        .findById(UUID.fromString(merchantDto.getUuid()))
        .orElseThrow();
    log.info("Merchant found {} with website {}", merchant.getName(), merchant.getUrl());
    final List<Category> categories = categoryCrawler.crawlUrl(merchant.getUrl(),
        new MapCrawlContext(null));

    log.info("Category Count: {}", categories.size());
    for (final Category category: categories) {
      log.info("Handling {}", category.toString());

      final Optional<MerchantInferredCategory> existingCategoryOpt = merchantInferredCategoryDao
          .findByMerchantUuidAndUrl(merchant.getUuid(), category.getCategoryUrl());

      if (existingCategoryOpt.isPresent()) {
        final MerchantInferredCategory existingCategory = existingCategoryOpt.get();
        log.info("Existing category found {}", existingCategory.getUuid());
        existingCategory.setName(category.getCategoryName());
        merchantInferredCategoryDao.save(existingCategory);
      } else {
        final MerchantInferredCategory newCategory =
            new MerchantInferredCategory(merchant, category);
        var result = merchantInferredCategoryDao.save(newCategory);
        log.info("New category saved {}", result.getUuid());
      }
    }
    merchant.setLastCrawledAt(OffsetDateTime.now(Clock.systemUTC()));
    merchantDao.save(merchant);
    log.info("End Category Crawl for merchant {}", merchantDto.getUuid());
  }
}
