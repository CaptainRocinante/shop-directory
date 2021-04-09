package com.rocinante.shops.service.sync;

import com.rocinante.shops.datastore.entities.Product;
import java.util.List;
import java.util.UUID;
import javax.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class SearchService {
  private final EntityManager entityManager;

  @Transactional(readOnly = true)
  public List<Product> search(final String query, final List<UUID> bnplFilters) {
    final SearchSession searchSession = Search.session(entityManager);
    return searchSession
        .search(Product.class)
        .where(
            f -> {
              var predicate = f.bool()
                  .must(f.match().field("name").matching(query))
                  .should(f.match().field("merchantInferredCategoryText").matching(query))
                  .should(f.match().field("merchantNameText").matching(query))
                  .should(f.match().field("bnplNameText").matching(query));
              if (!bnplFilters.isEmpty()) {
                var init = f.bool();
                for (UUID bnplFilter: bnplFilters) {
                  init = init.should(f.match().field("bnplUuids").matching(bnplFilter));
                }
                predicate.filter(init.minimumShouldMatchNumber(1));
              }
              return predicate;
            })
        .fetchHits(100);
  }
}
