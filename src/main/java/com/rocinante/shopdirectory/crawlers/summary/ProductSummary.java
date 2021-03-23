package com.rocinante.shopdirectory.crawlers.summary;

import javax.annotation.Nullable;
import lombok.Data;
import lombok.ToString;
import org.javamoney.moneta.FastMoney;

@Data
@ToString
public class ProductSummary {
  private final String url;
  private final String description;
  private final String imageSrcUrl;
  private final FastMoney originalPrice;
  @Nullable private final FastMoney salePrice;
}
