package com.rocinante.common.api.dto;

import com.opencsv.bean.CsvBindByPosition;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BnplCsvUploadDto {
  @CsvBindByPosition(position = 0)
  private String bnplProviderName;

  @CsvBindByPosition(position = 1)
  private String bnplProviderWebsite;
}
