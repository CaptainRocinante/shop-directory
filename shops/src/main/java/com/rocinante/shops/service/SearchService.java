package com.rocinante.shops.service;

import com.rocinante.common.api.dto.BnplFilterDto;
import com.rocinante.common.api.dto.MerchantFilterDto;
import com.rocinante.datastore.entities.BnplProvider;
import com.rocinante.datastore.entities.Merchant;
import com.rocinante.datastore.entities.MerchantInferredCategory;
import com.rocinante.datastore.entities.Product;
import com.rocinante.shops.search.SearchServiceQuery;
import com.rocinante.shops.search.SearchServiceResults;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
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
  private final BnplService bnplService;

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

    final List<BnplFilterDto> bnplFiltersList =
        bnplService.getAllBnplProviders().stream()
            .map(BnplProvider::toBnplFilterDto)
            .sorted((b1, b2) -> b1.getBnplName().compareToIgnoreCase(b2.getBnplName()))
            .collect(Collectors.toList());
    final List<MerchantFilterDto> merchantFiltersList;
    if (searchServiceQuery.getUserAppliedMerchantFilters() == null ||
        searchServiceQuery.getUserAppliedMerchantFilters().isEmpty()) {
      merchantFiltersList =
          searchFetchTop200ForBrandsFilter(searchServiceQuery)
              .stream()
              .map(Product::getMerchantInferredCategories)
              .map(Set::stream)
              .flatMap(Function.identity())
              .map(MerchantInferredCategory::getMerchant)
              .distinct()
              .sorted((m1, m2) -> m1.getName().compareToIgnoreCase(m2.getName()))
              .map(Merchant::toMerchantFilterDto)
              .collect(Collectors.toList());
    } else {
      merchantFiltersList = Collections.emptyList();
    }
    return new SearchServiceResults(
        productSearchResult.total().hitCountLowerBound(), productSearchResult.hits(),
        bnplFiltersList, merchantFiltersList);
  }

  private List<Product> searchFetchTop200ForBrandsFilter(final SearchServiceQuery searchServiceQuery) {
    final SearchSession searchSession = Search.session(entityManager);
    return
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
            .fetch(200)
            .hits();
  }
}
