package com.rocinante.shops.api;

import lombok.Data;

@Data
public class ProductCrudDto {
  private final String uuid;
  private final boolean enabled;
}
