package com.rocinante.shops.api;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BnplFilterDto {
  private final String bnplUuid;
  private final String bnplName;
}
