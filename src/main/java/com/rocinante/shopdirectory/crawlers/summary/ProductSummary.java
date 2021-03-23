package com.rocinante.shopdirectory.crawlers.summary;

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
  private final FastMoney salePrice;
}
