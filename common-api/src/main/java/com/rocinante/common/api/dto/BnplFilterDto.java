package com.rocinante.common.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BnplFilterDto {
  private final String bnplUuid;
  private final String bnplName;
}
