package com.rocinante.common.api.dto;

import lombok.Data;

@Data
public class MerchantCreateDto {
  private final String bnplUuid;
  private final String merchantName;
  private final String merchantUrl;
}
