package com.rocinante.crawlers.summary;

import com.google.common.collect.Range;
import java.math.BigDecimal;
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

  public String getCurrency() {
    return currentPrice.lowerEndpoint().getCurrency().getCurrencyCode();
  }

  public BigDecimal currentPriceLowerRange() {
    return BigDecimal.valueOf(currentPrice.lowerEndpoint().getNumber().doubleValue());
  }

  public BigDecimal currentPriceUpperRange() {
    return BigDecimal.valueOf(currentPrice.upperEndpoint().getNumber().doubleValue());
  }

  @Nullable
  public BigDecimal originalPriceLowerRange() {
    if (originalPrice == null) {
      return null;
    }
    return BigDecimal.valueOf(originalPrice.lowerEndpoint().getNumber().doubleValue());
  }

  @Nullable
  public BigDecimal originalPriceUpperRange() {
    if (originalPrice == null) {
      return null;
    }
    return BigDecimal.valueOf(originalPrice.upperEndpoint().getNumber().doubleValue());
  }

}
