package com.rocinante.shops.service;

import com.rocinante.crawlers.MapCrawlContext;
import com.rocinante.crawlers.category.Category;
import com.rocinante.crawlers.category.CategoryCrawler;
import com.rocinante.crawlers.summary.ProductSummary;
import com.rocinante.crawlers.summary.SummaryCrawler;
import com.rocinante.shops.datastore.dao.MerchantDao;
import com.rocinante.shops.datastore.dao.MerchantInferredCategoryDao;
import com.rocinante.shops.datastore.entities.Merchant;
import com.rocinante.shops.datastore.entities.MerchantInferredCategory;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class AsyncCrawlingService {
  private final MerchantDao merchantDao;
  private final MerchantInferredCategoryDao merchantInferredCategoryDao;
  private final CategoryCrawler categoryCrawler;
  private final SummaryCrawler summaryCrawler;

  @Async
  public void crawlAndSaveCategoriesForMerchant(UUID merchantUuid) {
    log.info("Begin Category Crawl for merchant {}", merchantUuid);
    final Merchant merchant = merchantDao
        .findById(merchantUuid)
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
    merchant.setLastCrawledAt(Instant.now().atOffset(ZoneOffset.UTC));
    merchantDao.save(merchant);
    log.info("End Category Crawl for merchant {}", merchantUuid);
  }

  @Async
  public void crawlAndSaveProductsForCategory(MerchantInferredCategory merchantInferredCategory) {
    log.info("Begin Product Crawl for category {}", merchantInferredCategory.getUuid());
    final List<ProductSummary> productSummaries =
        summaryCrawler.crawlUrl(merchantInferredCategory.getUrl(),
            new MapCrawlContext(null));

    log.info("Product Count: {}", productSummaries.size());

    for (final ProductSummary product: productSummaries) {
      log.info("Handling {}", product.toString());
    }
  }
}
