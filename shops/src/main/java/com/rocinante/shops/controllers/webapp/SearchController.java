package com.rocinante.shops.controllers.webapp;

import com.rocinante.shops.api.ProductDto;
import com.rocinante.shops.datastore.entities.Product;
import com.rocinante.shops.service.sync.SearchService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@AllArgsConstructor
public class SearchController {
  private final SearchService searchService;

  @RequestMapping("/search")
  public String search(Model model, @RequestParam final String query) {
    final List<ProductDto> productDtoList =
        searchService.search(query).stream()
            .map(Product::toProductDto)
            .collect(Collectors.toList());

    model.addAttribute("products", productDtoList);
    return "turboFragments/products";
  }
}
