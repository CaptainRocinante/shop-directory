package com.rocinante.shops.controllers.webapp;

import com.rocinante.common.api.dto.BnplFilterDto;
import com.rocinante.common.api.dto.MerchantFilterDto;
import com.rocinante.common.api.dto.ProductDto;
import com.rocinante.datastore.entities.BnplProvider;
import com.rocinante.datastore.entities.Merchant;
import com.rocinante.datastore.entities.MerchantInferredCategory;
import com.rocinante.datastore.entities.Product;
import com.rocinante.shops.service.BnplService;
import com.rocinante.shops.service.MerchantService;
import com.rocinante.shops.service.SearchService;
import com.rocinante.shops.service.SearchService.SearchServiceResults;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
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
  private final BnplService bnplService;
  private final MerchantService merchantService;

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
    log.info("Performing Search query: {} for page: {} with bnplFilters: {} with merchantFilters:"
            + " {}", query, page, bnplFiltersSelected, merchantFiltersSelected);
    if (page == null) {
      page = 1;
    }
    final List<UUID> bnplFilterForSearchService =
        bnplFiltersSelected == null
            ? Collections.emptyList()
            : bnplFiltersSelected.stream().map(UUID::fromString).collect(Collectors.toList());
    final SearchServiceResults searchServiceResults =
        searchService.search(
            query,
            bnplFilterForSearchService,
            merchantFiltersSelected == null
                ? Collections.emptyList()
                : merchantFiltersSelected.stream()
                    .map(UUID::fromString)
                    .collect(Collectors.toList()),
            page - 1,
            SINGLE_PAGE_RESULT_COUNT);
    final List<ProductDto> productDtoList =
        searchServiceResults.getCurrentPageResults().stream()
            .map(Product::toProductDto)
            .collect(Collectors.toList());
    final List<BnplFilterDto> bnplFiltersList =
        bnplService.getAllBnplProviders().stream()
            .map(BnplProvider::toBnplFilterDto)
            .sorted((b1, b2) -> b1.getBnplName().compareToIgnoreCase(b2.getBnplName()))
            .collect(Collectors.toList());
    final List<BnplFilterDto> bnplFiltersSelectedList =
        bnplFiltersSelected == null
            ? Collections.emptyList()
            : bnplFiltersList.stream()
                .filter(b -> bnplFiltersSelected.contains(b.getBnplUuid()))
                .collect(Collectors.toList());
    //    productDtoList.forEach(p -> log.info(p.toString()));
    final List<MerchantFilterDto> merchantFiltersList;
    if (merchantFiltersSelected == null || merchantFiltersSelected.isEmpty()) {
      final SearchServiceResults top200Search =
          searchService.searchFetchTop200(query, bnplFilterForSearchService);
      merchantFiltersList =
          top200Search.getCurrentPageResults().stream()
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

    final List<MerchantFilterDto> merchantFiltersSelectedList =
        merchantFiltersSelected == null
            ? Collections.emptyList()
            : merchantService
                .getAllMerchantsForUuids(
                    merchantFiltersSelected.stream()
                        .map(UUID::fromString)
                        .collect(Collectors.toList()))
                .stream()
                .map(Merchant::toMerchantFilterDto)
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
    model.addAttribute("bnplFilters", bnplFiltersList);
    model.addAttribute("bnplFiltersSelected", bnplFiltersSelectedList);
    model.addAttribute("merchantFilters", merchantFiltersList);
    model.addAttribute("merchantFiltersSelected", merchantFiltersSelectedList);
    return "searchResults";
  }
}
