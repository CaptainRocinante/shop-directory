package com.rocinante.shops.controllers.webapp;

import com.rocinante.shops.api.BnplFilterDto;
import com.rocinante.shops.api.ProductDto;
import com.rocinante.shops.datastore.entities.BnplProvider;
import com.rocinante.shops.datastore.entities.Product;
import com.rocinante.shops.service.sync.BnplService;
import com.rocinante.shops.service.sync.SearchService;
import java.util.Collections;
import java.util.List;
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

  @RequestMapping("/search")
  public String search(Model model,
      @RequestParam final String query,
      @RequestParam(required = false) List<String> bnplFiltersSelected) {
    final List<ProductDto> productDtoList =
        searchService.search(query).stream()
            .map(Product::toProductDto)
            .collect(Collectors.toList());
    final List<BnplFilterDto> bnplFilterList =
        bnplService
            .getAllBnplProviders()
            .stream()
            .map(BnplProvider::toBnplFilterDto)
            .sorted((b1, b2) -> b1.getBnplName().compareToIgnoreCase(b2.getBnplName()))
            .collect(Collectors.toList());
//    productDtoList.forEach(p -> log.info(p.toString()));

    model.addAttribute("query", query);
    model.addAttribute("products", productDtoList);
    model.addAttribute("bnplFilters", bnplFilterList);
    model.addAttribute("bnplFiltersSelected", bnplFiltersSelected != null
        ? bnplFiltersSelected : Collections.emptyList());
    return "searchResults";
  }
}
