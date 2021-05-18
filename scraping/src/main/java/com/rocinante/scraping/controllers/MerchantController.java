package com.rocinante.scraping.controllers;

import com.rocinante.common.api.dto.MerchantCreateDto;
import com.rocinante.common.api.dto.MerchantCrudDto;
import com.rocinante.scraping.service.sync.MerchantService;
import java.net.MalformedURLException;
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

  @PostMapping("create")
  public String create(@RequestBody MerchantCreateDto merchantCreateDto)
      throws MalformedURLException {
    return merchantService.createMerchant(merchantCreateDto);
  }
}
