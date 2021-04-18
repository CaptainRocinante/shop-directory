package com.rocinante.common.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MerchantFilterDto {
  private final String merchantUuid;
  private final String merchantName;
}
