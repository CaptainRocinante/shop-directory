package com.rocinante.shops.api;

import java.util.List;
import javax.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductDto {
  private final String uuid;
  private final String name;
  private final String url;
  private final String currencySymbol;
  private final String currentPriceLowerRange;
  private final String currentPriceUpperRange;
  @Nullable private final String originalPriceLowerRange;
  @Nullable private final String originalPriceUpperRange;
  private final String mainImageUrl;
  private final List<String> merchantNames;
  private final List<String> bnplProviders;
}
