package com.rocinante.scraping.controllers;

import com.rocinante.scraping.service.async.AsyncIndexingService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("index")
public class IndexController {
  private final AsyncIndexingService asyncIndexingService;

  @PostMapping("/create")
  public void indexAllData() throws InterruptedException {
    asyncIndexingService.reindexAllProducts();
  }
}
