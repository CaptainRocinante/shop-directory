package com.rocinante.shops.api;

import lombok.Data;

@Data
public class MerchantCrudDto {
  private final String uuid;
  private final boolean enabled;
}
