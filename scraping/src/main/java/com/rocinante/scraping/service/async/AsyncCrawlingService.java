package com.rocinante.scraping.service.async;

import com.rocinante.common.api.crawlers.Category;
import com.rocinante.common.api.crawlers.ProductSummary;
import com.rocinante.datastore.dao.MerchantDao;
import com.rocinante.datastore.dao.MerchantInferredCategoryDao;
import com.rocinante.datastore.dao.ProductDao;
import com.rocinante.datastore.entities.Merchant;
import com.rocinante.datastore.entities.MerchantInferredCategory;
import com.rocinante.datastore.entities.Product;
import com.rocinante.scraping.crawlers.MapCrawlContext;
import com.rocinante.scraping.crawlers.category.CategoryCrawler;
import com.rocinante.scraping.crawlers.summary.SummaryCrawler;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Slf4j
public class AsyncCrawlingService {
  private final MerchantDao merchantDao;
  private final MerchantInferredCategoryDao merchantInferredCategoryDao;
  private final ProductDao productDao;
  private final CategoryCrawler categoryCrawler;
  private final SummaryCrawler summaryCrawler;

  @Async
  @Transactional
  public void crawlAndSaveCategoriesForMerchant(UUID merchantUuid) throws MalformedURLException {
    final Merchant merchant = merchantDao.getOne(merchantUuid);
    log.info("Begin Category Crawl for merchant {} {}", merchant.getName(), merchant.getUrl());
    final List<Category> categories =
        categoryCrawler.crawlUrl(merchant.getUrl().toString(), new MapCrawlContext(null));

    log.info("Category Count: {}", categories.size());
    for (final Category category : categories) {
      log.info("Handling {}", category.toString());

      final Optional<MerchantInferredCategory> existingCategoryOpt =
          merchantInferredCategoryDao.findByMerchantUuidAndUrl(
              merchant.getUuid(), new URL(category.getCategoryUrl()));

      if (existingCategoryOpt.isPresent()) {
        final MerchantInferredCategory existingCategory = existingCategoryOpt.get();
        log.info(
            "Existing category found {} {}", existingCategory.getUuid(), existingCategory.getUrl());
        if (existingCategory.applyUpdatesIfNeeded(category)) {
          log.info(
              "Updates for category detected, saving to DB {} {}",
              existingCategory.getUuid(),
              existingCategory.getUrl());
          merchantInferredCategoryDao.save(existingCategory);
        }
      } else {
        final MerchantInferredCategory newCategory;
        newCategory = new MerchantInferredCategory(merchant, category);
        var result = merchantInferredCategoryDao.save(newCategory);
        log.info("New category saved {} {}", result.getUuid(), result.getUrl());
      }
    }
    merchant.setLastCrawledAt(Instant.now().atOffset(ZoneOffset.UTC));
    merchantDao.save(merchant);
    log.info("End Category Crawl for merchant {} {}", merchant.getUuid(), merchant.getUrl());
  }

  @Async
  @Transactional
  public void crawlAndSaveProductsForCategory(UUID merchantInferredCategoryUuid)
      throws MalformedURLException {
    final MerchantInferredCategory merchantInferredCategory =
        merchantInferredCategoryDao.getOne(merchantInferredCategoryUuid);
    log.info(
        "Begin Product Crawl for category {} {}",
        merchantInferredCategory.getUuid(),
        merchantInferredCategory.getUrl());
    final List<ProductSummary> productSummaries =
        summaryCrawler.crawlUrl(
            merchantInferredCategory.getUrl().toString(), new MapCrawlContext(null));

    log.info("Product Count: {}", productSummaries.size());

    for (final ProductSummary productSummary : productSummaries) {
      log.info("Handling {}", productSummary.toString());

      final Product product;
      final Optional<Product> existingProduct =
          productDao.findByUrl(new URL(productSummary.getUrl()));

      if (existingProduct.isPresent()) {
        product = existingProduct.get();
        log.info("Existing product found {} {}", product.getUuid(), product.getUrl());
        boolean isUpdated = product.applyUpdatesIfNeeded(productSummary);
        if (isUpdated) {
          log.info(
              "Updates for product detected, saving to DB {} {}",
              product.getUuid(),
              product.getUrl());
          productDao.save(product);
        }
      } else {
        product = productDao.save(new Product(productSummary));
        log.info("New product created, saved to DB {} {}", product.getUuid(), product.getUrl());
      }
      merchantInferredCategory.addProduct(product);
    }
    merchantInferredCategory.setLastCrawledAt(Instant.now().atOffset(ZoneOffset.UTC));
    merchantInferredCategoryDao.save(merchantInferredCategory);
    log.info(
        "End Product Crawl for category {} {}",
        merchantInferredCategory.getUuid(),
        merchantInferredCategory.getUrl());
  }
}
