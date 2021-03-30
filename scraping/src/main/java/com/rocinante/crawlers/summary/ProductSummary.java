package com.rocinante.crawlers.summary;

import com.google.common.collect.Range;
import java.util.List;
import javax.annotation.Nullable;
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
  @Nullable private final Range<FastMoney> originalPrice;
  private final Range<FastMoney> currentPrice;
}
