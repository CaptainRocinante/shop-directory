package com.rocinante.shops.service;

import com.rocinante.crawlers.MapCrawlContext;
import com.rocinante.crawlers.category.Category;
import com.rocinante.crawlers.category.CategoryCrawler;
import com.rocinante.crawlers.summary.ProductSummary;
import com.rocinante.crawlers.summary.SummaryCrawler;
import com.rocinante.shops.datastore.dao.MerchantDao;
import com.rocinante.shops.datastore.dao.MerchantInferredCategoryDao;
import com.rocinante.shops.datastore.dao.ProductDao;
import com.rocinante.shops.datastore.entities.Merchant;
import com.rocinante.shops.datastore.entities.MerchantInferredCategory;
import com.rocinante.shops.datastore.entities.Product;
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
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class AsyncCrawlingService {
  private final MerchantDao merchantDao;
  private final MerchantInferredCategoryDao merchantInferredCategoryDao;
  private final ProductDao productDao;
  private final CategoryCrawler categoryCrawler;
  private final SummaryCrawler summaryCrawler;

  @Async
  public void crawlAndSaveCategoriesForMerchant(UUID merchantUuid) throws MalformedURLException {
    log.info("Begin Category Crawl for merchant {}", merchantUuid);
    final Merchant merchant = merchantDao
        .findById(merchantUuid)
        .orElseThrow();
    log.info("Merchant found {} with website {}", merchant.getName(), merchant.getUrl());
    final List<Category> categories = categoryCrawler.crawlUrl(merchant.getUrl().toString(),
        new MapCrawlContext(null));

    log.info("Category Count: {}", categories.size());
    for (final Category category: categories) {
      log.info("Handling {}", category.toString());

      final Optional<MerchantInferredCategory> existingCategoryOpt = merchantInferredCategoryDao
          .findByMerchantUuidAndUrl(merchant.getUuid(), new URL(category.getCategoryUrl()));

      if (existingCategoryOpt.isPresent()) {
        final MerchantInferredCategory existingCategory = existingCategoryOpt.get();
        log.info("Existing category found {}", existingCategory.getUuid());
        existingCategory.updateFromLatestCrawl(merchant, category);
        merchantInferredCategoryDao.save(existingCategory);
      } else {
        final MerchantInferredCategory newCategory;
        newCategory = new MerchantInferredCategory(merchant, category);
        var result = merchantInferredCategoryDao.save(newCategory);
        log.info("New category saved {}", result.getUuid());
      }
    }
    merchant.setLastCrawledAt(Instant.now().atOffset(ZoneOffset.UTC));
    merchantDao.save(merchant);
    log.info("End Category Crawl for merchant {}", merchantUuid);
  }

  @Async
  public void crawlAndSaveProductsForCategory(MerchantInferredCategory merchantInferredCategory)
      throws MalformedURLException {
    log.info("Begin Product Crawl for category {} {}", merchantInferredCategory.getUuid(),
        merchantInferredCategory.getUrl());
    final List<ProductSummary> productSummaries =
        summaryCrawler.crawlUrl(merchantInferredCategory.getUrl().toString(),
            new MapCrawlContext(null));

    log.info("Product Count: {}", productSummaries.size());

    for (final ProductSummary product: productSummaries) {
      log.info("Handling {}", product.toString());

      final Optional<Product> existingProductOpt = productDao.findByUrl(new URL(product.getUrl()));

      if (existingProductOpt.isPresent()) {
        final Product existingProduct = existingProductOpt.get();
        log.info("Existing product found {}", existingProduct.getUuid());
        if (!existingProduct.isSameAsLastCrawl(product)) {
          existingProduct.updateFromLatestCrawl(product, merchantInferredCategory);
          productDao.save(existingProduct);
          log.info("Existing product updated {}", existingProduct.getUuid());
        } else {
          log.info("Existing product has no changes in latest crawl {}", existingProduct.getUuid());
        }
      } else {
        final Product newProduct;
        newProduct = new Product(product, merchantInferredCategory);
        var result = productDao.save(newProduct);
        log.info("New Product saved {}", result.getUuid());
      }
    }
    merchantInferredCategory.setLastCrawledAt(Instant.now().atOffset(ZoneOffset.UTC));
    merchantInferredCategoryDao.save(merchantInferredCategory);
    log.info("End Product Crawl for category {}", merchantInferredCategory.getUuid());
  }
}
