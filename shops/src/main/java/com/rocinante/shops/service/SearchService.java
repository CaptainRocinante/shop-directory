package com.rocinante.shops.service;

import com.rocinante.datastore.entities.Product;
import com.rocinante.shops.search.SearchServiceQuery;
import com.rocinante.shops.search.SearchServiceResults;
import java.util.UUID;
import javax.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class SearchService {
  private final EntityManager entityManager;

  @Transactional(readOnly = true)
  public SearchServiceResults search(
      final SearchServiceQuery searchServiceQuery,
      int zeroBasedPageNumber,
      int pageResultCount) {
    final SearchSession searchSession = Search.session(entityManager);
    final SearchResult<Product> productSearchResult =
        searchSession
            .search(Product.class)
            .where(
                f -> {
                  var predicate =
                      f.bool()
                          .must(f.match().field("enabled").matching(true))
                          .must(f.match().field("merchantEnabled").matching(true));

                  searchServiceQuery
                      .getSingleFieldSearchQueryParamList()
                      .forEach(qp -> {
                        var predicateStep = f.match()
                            .field(qp.getSearchIndexedField().getFieldName())
                            .matching(searchServiceQuery.getQuery())
                            .boost(qp.getWeight());
                        if (qp.isRequired()) {
                          predicate.must(predicateStep);
                        } else {
                          predicate.should(predicateStep);
                        }
                      });

                  if (!searchServiceQuery.getUserAppliedBnplFilters().isEmpty()) {
                    var bnplFilterPredicate = f.bool();
                    for (final UUID bnplFilter : searchServiceQuery.getUserAppliedBnplFilters()) {
                      bnplFilterPredicate =
                          bnplFilterPredicate.should(
                              f.match().field("bnplUuids").matching(bnplFilter));
                    }
                    predicate.filter(bnplFilterPredicate.minimumShouldMatchNumber(1));
                  }
                  if (!searchServiceQuery.getUserAppliedMerchantFilters().isEmpty()) {
                    var merchantFilterPredicate = f.bool();
                    for (final UUID merchantFilter : searchServiceQuery.getUserAppliedMerchantFilters()) {
                      merchantFilterPredicate =
                          merchantFilterPredicate.should(
                              f.match().field("merchantUuids").matching(merchantFilter));
                    }
                    predicate.filter(merchantFilterPredicate.minimumShouldMatchNumber(1));
                  }
                  return predicate;
                })
            .totalHitCountThreshold(1000)
            .fetch(zeroBasedPageNumber * pageResultCount, pageResultCount);

    return new SearchServiceResults(
        productSearchResult.total().hitCountLowerBound(), productSearchResult.hits());
  }

  @Transactional(readOnly = true)
  public SearchServiceResults searchFetchTop200(final SearchServiceQuery searchServiceQuery) {
    final SearchSession searchSession = Search.session(entityManager);
    final SearchResult<Product> productSearchResult =
        searchSession
            .search(Product.class)
            .where(
                f -> {
                  var predicate =
                      f.bool()
                          .must(f.match().field("enabled").matching(true))
                          .must(f.match().field("merchantEnabled").matching(true));

                  searchServiceQuery
                      .getSingleFieldSearchQueryParamList()
                      .forEach(qp -> {
                        var predicateStep = f.match()
                            .field(qp.getSearchIndexedField().getFieldName())
                            .matching(searchServiceQuery.getQuery())
                            .boost(qp.getWeight());
                        if (qp.isRequired()) {
                          predicate.must(predicateStep);
                        } else {
                          predicate.should(predicateStep);
                        }
                      });

                  if (!searchServiceQuery.getUserAppliedBnplFilters().isEmpty()) {
                    var bnplFilterPredicate = f.bool();
                    for (final UUID bnplFilter : searchServiceQuery.getUserAppliedBnplFilters()) {
                      bnplFilterPredicate =
                          bnplFilterPredicate.should(
                              f.match().field("bnplUuids").matching(bnplFilter));
                    }
                    predicate.filter(bnplFilterPredicate.minimumShouldMatchNumber(1));
                  }
                  return predicate;
                })
            .totalHitCountThreshold(200)
            .fetch(200);

    return new SearchServiceResults(
        productSearchResult.total().hitCountLowerBound(), productSearchResult.hits());
  }
}
