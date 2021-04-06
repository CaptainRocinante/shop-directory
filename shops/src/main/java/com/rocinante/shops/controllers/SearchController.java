package com.rocinante.shops.controllers;

import com.rocinante.shops.api.ProductDto;
import com.rocinante.shops.datastore.entities.Product;
import com.rocinante.shops.service.SearchService;
import com.rocinante.shops.service.async.AsyncIndexingService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
public class SearchController {
  private final AsyncIndexingService asyncIndexingService;
  private final SearchService searchService;

  @PostMapping("index")
  public void indexAllData() throws InterruptedException {
    asyncIndexingService.reindexAllProducts();
  }

  @GetMapping
  public List<ProductDto> search(@RequestParam final String query) {
    return searchService
        .search(query)
        .stream()
        .map(Product::toProductDto)
        .collect(Collectors.toList());
  }
}
