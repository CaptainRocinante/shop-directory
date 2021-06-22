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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@AllArgsConstructor
@Slf4j
public class SearchController {
  private static final int SINGLE_PAGE_RESULT_COUNT = 50;

  private final SearchService searchService;

  private SearchServiceQuery getDefaultSearchServiceQuery(
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

  @RequestMapping("/")
  public String index(Model model) {
    log.info("Landing Page Impression");
    model.addAttribute("query", "");
    return "index";
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
        getDefaultSearchServiceQuery(query, bnplFiltersSelected, merchantFiltersSelected);
    final SearchServiceResults searchServiceResults =
        searchService.search(defaultSearchQuery, page - 1, SINGLE_PAGE_RESULT_COUNT);
    final List<ProductDto> productDtoList =
        searchServiceResults.getCurrentPageResults().stream()
            .map(Product::toProductDto)
            .collect(Collectors.toList());

    model.addAttribute("query", query);
    model.addAttribute("products", productDtoList);
    model.addAttribute("page", page);
    model.addAttribute(
        "totalPageCount",
        Math.max(
            1,
            Math.min(
                10,
                (int)
                    Math.ceil(
                        (1.0d * searchServiceResults.getTotalResultsCount())
                            / SINGLE_PAGE_RESULT_COUNT))));
    model.addAttribute("totalResultsCount", searchServiceResults.getTotalResultsCount());
    model.addAttribute("bnplFilters", searchServiceResults.getBnplFilterDtos());
    model.addAttribute("bnplFiltersSelected", searchServiceResults.getBnplFiltersSelectedDtos());
    model.addAttribute("merchantFilters", searchServiceResults.getMerchantFilterDtos());
    model.addAttribute(
        "merchantFiltersSelected", searchServiceResults.getMerchantSelectedFilterDtos());
    return "searchResults";
  }
}
