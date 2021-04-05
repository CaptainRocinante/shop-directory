package com.rocinante.shops.controllers;

import com.rocinante.shops.service.AsyncIndexingService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
public class SearchController {
  private final AsyncIndexingService asyncIndexingService;

  @PostMapping("index")
  public void indexAllData() throws InterruptedException {
    asyncIndexingService.reindexAllProducts();
  }
}
