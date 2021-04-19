package com.rocinante.client;

import com.opencsv.bean.CsvBindByPosition;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MerchantCsvDto {
  @CsvBindByPosition(position = 0)
  private String merchantUuid;
}
