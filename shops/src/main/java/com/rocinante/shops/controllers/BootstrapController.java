package com.rocinante.shops.controllers;

import com.rocinante.shops.service.async.AsyncBnplMerchantUploadService;
import java.io.FileNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/bootstrap", produces = MediaType.APPLICATION_JSON_VALUE)
public class BootstrapController {
  private final AsyncBnplMerchantUploadService bnplMerchantUploadService;

  @PostMapping("/setup/bnpls")
  public void setupBnpls() throws FileNotFoundException {
    bnplMerchantUploadService.setupBnplsAndUploadMerchantsIdempotent();
  }
}
