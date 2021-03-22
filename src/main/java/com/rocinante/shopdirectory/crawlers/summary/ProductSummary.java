package com.rocinante.shopdirectory.crawlers.summary;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ProductSummary {
  private final String url;
  private final String description;
  private final String imageSrcUrl;
  private final String price;
}
