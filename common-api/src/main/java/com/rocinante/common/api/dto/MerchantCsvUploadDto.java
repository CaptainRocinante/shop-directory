package com.rocinante.common.api.dto;

import com.opencsv.bean.CsvBindByPosition;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MerchantCsvUploadDto {
  @CsvBindByPosition(position = 0)
  private String merchantName;

  @CsvBindByPosition(position = 1)
  private String merchantWebsite;
}
