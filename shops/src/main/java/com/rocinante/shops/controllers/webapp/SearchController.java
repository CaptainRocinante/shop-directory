package com.rocinante.shops.controllers.webapp;

import com.rocinante.shops.api.BnplFilterDto;
import com.rocinante.shops.api.MerchantFilterDto;
import com.rocinante.shops.api.ProductDto;
import com.rocinante.shops.datastore.entities.BnplProvider;
import com.rocinante.shops.datastore.entities.Merchant;
import com.rocinante.shops.datastore.entities.MerchantInferredCategory;
import com.rocinante.shops.datastore.entities.Product;
import com.rocinante.shops.service.sync.BnplService;
import com.rocinante.shops.service.sync.MerchantService;
import com.rocinante.shops.service.sync.SearchService;
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
  private final SearchService searchService;
  private final BnplService bnplService;
  private final MerchantService merchantService;

  @RequestMapping("/search")
  public String search(Model model,
      @RequestParam final String query,
      @RequestParam(required = false) List<String> bnplFiltersSelected,
      @RequestParam(required = false) List<String> merchantFiltersSelected) {
    final List<Product> productList = searchService
        .search(query,
            bnplFiltersSelected == null ? Collections.emptyList() :
                bnplFiltersSelected
                    .stream()
                    .map(UUID::fromString)
                    .collect(Collectors.toList()),
            merchantFiltersSelected == null ? Collections.emptyList() :
                merchantFiltersSelected
                    .stream()
                    .map(UUID::fromString)
                    .collect(Collectors.toList())
        );
    final List<ProductDto> productDtoList =
        productList
            .stream()
            .map(Product::toProductDto)
            .collect(Collectors.toList());
    final List<BnplFilterDto> bnplFiltersList =
        bnplService
            .getAllBnplProviders()
            .stream()
            .map(BnplProvider::toBnplFilterDto)
            .sorted((b1, b2) -> b1.getBnplName().compareToIgnoreCase(b2.getBnplName()))
            .collect(Collectors.toList());
    final List<BnplFilterDto> bnplFiltersSelectedList = bnplFiltersSelected == null ?
        Collections.emptyList() :
        bnplFiltersList
            .stream()
            .filter(b -> bnplFiltersSelected.contains(b.getBnplUuid()))
            .collect(Collectors.toList());
    //    productDtoList.forEach(p -> log.info(p.toString()));
    final List<MerchantFilterDto> merchantFiltersList =
        productList.stream()
            .map(Product::getMerchantInferredCategories)
            .map(Set::stream)
            .flatMap(Function.identity())
            .map(MerchantInferredCategory::getMerchant)
            .distinct()
            .sorted((m1, m2) -> m1.getName().compareToIgnoreCase(m2.getName()))
            .map(Merchant::toMerchantFilterDto)
            .collect(Collectors.toList());
    final List<MerchantFilterDto> merchantFiltersSelectedList =
        merchantFiltersSelected == null ? Collections.emptyList() :
            merchantService
                .getAllMerchantsForUuids(
                    merchantFiltersSelected
                        .stream()
                        .map(UUID::fromString)
                        .collect(Collectors.toList()))
                .stream()
                .map(Merchant::toMerchantFilterDto)
                .collect(Collectors.toList());

    model.addAttribute("query", query);
    model.addAttribute("products", productDtoList);
    model.addAttribute("bnplFilters", bnplFiltersList);
    model.addAttribute("bnplFiltersSelected", bnplFiltersSelectedList);
    model.addAttribute("merchantFilters", merchantFiltersList);
    model.addAttribute("merchantFiltersSelected", merchantFiltersSelectedList);
    return "searchResults";
  }
}
