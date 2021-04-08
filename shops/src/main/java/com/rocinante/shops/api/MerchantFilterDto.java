package com.rocinante.shops.api;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MerchantFilterDto {
  private final String merchantUuid;
  private final String merchantName;
}
