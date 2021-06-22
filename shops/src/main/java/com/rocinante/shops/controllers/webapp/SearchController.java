package com.rocinante.shops.controllers.webapp;

import com.rocinante.common.api.dto.ProductDto;
import com.rocinante.datastore.entities.Product;
import com.rocinante.shops.search.SearchIndexedField;
import com.rocinante.shops.search.SearchServiceQuery;
import com.rocinante.shops.search.SearchServiceResults;
import com.rocinante.shops.search.SingleFieldSearchQueryParam;
import com.rocinante.shops.service.SearchService;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@AllArgsConstructor
@Slf4j
public class SearchController {
  private static final int SINGLE_PAGE_RESULT_COUNT = 50;

  private final SearchService searchService;

  private SearchServiceQuery createDefaultSearchServiceQuery(
      String query,
      @Nullable List<String> bnplFiltersSelected,
      @Nullable List<String> merchantFiltersSelected) {
    return new SearchServiceQuery.Builder(query)
        .addQueryParam(new SingleFieldSearchQueryParam(SearchIndexedField.NAME, 2.0f, false))
        .addQueryParam(
            new SingleFieldSearchQueryParam(SearchIndexedField.MERCHANT_CATEGORY, 1.0f, false))
        .addQueryParam(
            new SingleFieldSearchQueryParam(SearchIndexedField.MERCHANT_NAME, 1.0f, false))
        .addQueryParam(new SingleFieldSearchQueryParam(SearchIndexedField.BNPL_NAME, 1.0f, false))
        .addUserAppliedBnplFilters(bnplFiltersSelected)
        .addUserAppliedMerchantFilters(merchantFiltersSelected)
        .build();
  }

  private SearchServiceQuery createCategorySearchServiceQuery(
      String query,
      @Nullable List<String> bnplFiltersSelected,
      @Nullable List<String> merchantFiltersSelected) {
    return new SearchServiceQuery.Builder(query)
        .addQueryParam(
            new SingleFieldSearchQueryParam(SearchIndexedField.MERCHANT_CATEGORY, 1.0f, false))
        .addUserAppliedBnplFilters(bnplFiltersSelected)
        .addUserAppliedMerchantFilters(merchantFiltersSelected)
        .build();
  }

  private int getPageCountFromTotalResultCount(long totalResultCount) {
    return Math.max(1,
        Math.min(10, (int) Math.ceil((1.0d * totalResultCount) / SINGLE_PAGE_RESULT_COUNT)));
  }

  private void performSearchAndPopulateModel(
      Model model,
      SearchServiceQuery searchServiceQuery,
      int pageNumber) {
    final SearchServiceResults searchServiceResults =
        searchService.search(searchServiceQuery,
            pageNumber - 1,
            SINGLE_PAGE_RESULT_COUNT);
    final List<ProductDto> productDtoList =
        searchServiceResults.getCurrentPageResults().stream()
            .map(Product::toProductDto)
            .collect(Collectors.toList());

    model.addAttribute("query", searchServiceQuery.getQuery());
    model.addAttribute("products", productDtoList);
    model.addAttribute("page", pageNumber);
    model.addAttribute("totalPageCount", getPageCountFromTotalResultCount(searchServiceResults.getTotalResultsCount()));
    model.addAttribute("totalResultsCount", searchServiceResults.getTotalResultsCount());
    model.addAttribute("bnplFilters", searchServiceResults.getBnplFilterDtos());
    model.addAttribute("bnplFiltersSelected", searchServiceResults.getBnplFiltersSelectedDtos());
    model.addAttribute("merchantFilters", searchServiceResults.getMerchantFilterDtos());
    model.addAttribute("merchantFiltersSelected", searchServiceResults.getMerchantSelectedFilterDtos());
  }

  @RequestMapping("/search")
  public String search(
      Model model,
      @RequestParam final String query,
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) List<String> bnplFiltersSelected,
      @RequestParam(required = false) List<String> merchantFiltersSelected) {
    log.info(
        "Performing Search query: {} for page: {} with bnplFilters: {} with merchantFilters:"
            + " {}",
        query,
        page,
        bnplFiltersSelected,
        merchantFiltersSelected);
    if (page == null) {
      page = 1;
    }
    final SearchServiceQuery defaultSearchQuery =
        createDefaultSearchServiceQuery(query, bnplFiltersSelected, merchantFiltersSelected);
    performSearchAndPopulateModel(model, defaultSearchQuery, page);
    return "searchResults";
  }

  @RequestMapping("/category/{categoryName}")
  public String categorySearch(
      Model model,
      @PathVariable String categoryName,
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) List<String> bnplFiltersSelected,
      @RequestParam(required = false) List<String> merchantFiltersSelected) {
    log.info(
        "Performing Category query: {} for page: {} with bnplFilters: {} with merchantFilters: {}",
        categoryName,
        page,
        bnplFiltersSelected,
        merchantFiltersSelected);
    if (page == null) {
      page = 1;
    }
    final SearchServiceQuery categorySearchQuery =
        createCategorySearchServiceQuery(categoryName, bnplFiltersSelected,
            merchantFiltersSelected);
    performSearchAndPopulateModel(model, categorySearchQuery, page);
    // Don't need to populate search bar for category results page
    model.addAttribute("query", "");
    return "searchResults";
  }
}
