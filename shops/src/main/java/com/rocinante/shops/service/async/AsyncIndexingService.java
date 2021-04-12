package com.rocinante.shops.service.async;

import com.rocinante.shops.datastore.dao.MerchantDao;
import com.rocinante.shops.datastore.dao.MerchantInferredCategoryDao;
import com.rocinante.shops.datastore.entities.Merchant;
import com.rocinante.shops.datastore.entities.MerchantInferredCategory;
import com.rocinante.shops.datastore.entities.Product;
import java.util.Set;
import java.util.UUID;
import javax.persistence.EntityManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.massindexing.MassIndexer;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.hibernate.search.mapper.orm.work.SearchIndexingPlan;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Slf4j
public class AsyncIndexingService {
  private final EntityManager entityManager;
  private final MerchantInferredCategoryDao merchantInferredCategoryDao;

  @Async
  @Transactional(readOnly = true)
  public void reindexAllProducts() throws InterruptedException {
    final SearchSession searchSession = Search.session(entityManager);
    final MassIndexer massIndexer =
        searchSession.massIndexer(Product.class).threadsToLoadObjects(5);
    massIndexer.startAndWait();
  }

  @Async
  @Transactional(readOnly = true)
  public void reindexAllProductsForMerchantInferredCategory(UUID categoryUuid) {
    final SearchSession searchSession = Search.session(entityManager);
    final SearchIndexingPlan indexingPlan = searchSession.indexingPlan();

    final MerchantInferredCategory category =
        merchantInferredCategoryDao.getOne(categoryUuid);
    log.info("Re-indexing category {} {}", category.getUuid(), category.getUrl());
    final Set<Product> products = category.getProducts();

    for (final Product product : products) {
      log.info("Re-indexing product {} {}", product.getUuid(), product.getUrl());
      indexingPlan.addOrUpdate(product);
    }
    indexingPlan.execute();
  }
}
