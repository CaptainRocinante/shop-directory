package com.rocinante.common.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MerchantCreateDto {
  private final String bnplUuid;
  private final String merchantName;
  private final String merchantUrl;
}
