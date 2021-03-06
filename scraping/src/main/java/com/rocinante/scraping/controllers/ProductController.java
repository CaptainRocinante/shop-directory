package com.rocinante.scraping.controllers;

import com.rocinante.common.api.dto.ProductCrudDto;
import com.rocinante.scraping.service.sync.ProductService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/product", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class ProductController {
  private final ProductService productService;

  @PostMapping("update")
  public void update(@RequestBody ProductCrudDto productCrudDto) {
    productService.updateProduct(productCrudDto);
  }
}
