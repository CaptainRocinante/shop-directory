package com.rocinante.shopdirectory.crawlers.summary;

import com.google.common.collect.Range;
import java.util.List;
import lombok.Data;
import lombok.ToString;
import org.javamoney.moneta.FastMoney;

@Data
@ToString
public class ProductSummary {
  private final String url;
  private final String inferredDescription;
  private final List<String> productImages;
  private final List<String> additionalImages;
  private final Range<FastMoney> originalPrice;
  private final Range<FastMoney> salePrice;
}
