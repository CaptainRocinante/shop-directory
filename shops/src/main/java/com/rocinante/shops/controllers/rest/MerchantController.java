package com.rocinante.shops.controllers.rest;

import com.rocinante.shops.api.MerchantCrudDto;
import com.rocinante.shops.service.sync.MerchantService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/merchant", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class MerchantController {
  private final MerchantService merchantService;

  @PostMapping("update")
  public void update(@RequestBody MerchantCrudDto merchantCrudDto) {
    merchantService.updateMerchant(merchantCrudDto);
  }
}
