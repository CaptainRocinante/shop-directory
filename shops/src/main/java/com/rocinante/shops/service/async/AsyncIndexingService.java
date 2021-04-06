package com.rocinante.shops.service.async;

import com.rocinante.shops.datastore.entities.Product;
import javax.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.massindexing.MassIndexer;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AsyncIndexingService {
  private final EntityManager entityManager;

  @Async
  @Transactional(readOnly = true)
  public void reindexAllProducts() throws InterruptedException {
    final SearchSession searchSession = Search.session(entityManager);
    final MassIndexer massIndexer =
        searchSession.massIndexer(Product.class).threadsToLoadObjects(5);
    massIndexer.startAndWait();
  }
}
