package com.rocinante.common.api.dto;

import lombok.Data;

@Data
public class ProductCrudDto {
  private final String uuid;
  private final boolean enabled;
}
