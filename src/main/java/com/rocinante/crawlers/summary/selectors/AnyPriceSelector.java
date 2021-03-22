package com.rocinante.crawlers.summary.selectors;

import com.rocinante.crawlers.infrastructure.ElementSelector;
import java.util.Objects;
import java.util.regex.Pattern;
import org.jsoup.nodes.Element;

public class AnyPriceSelector implements ElementSelector {
  private static final Pattern pricePattern = Pattern.compile("(USD|EUR|€|\\$|£)\\s*(\\d{1,3}(?:["
      + ".,]\\d{3})*(?:[.,]\\d{2})?)|(\\d{1,3}(?:[.,]\\d{3})*(?:[.,]\\d{2})?)\\s*?(USD|EUR|€|\\$|£)");

  @Override
  public boolean select(Element element) {
    return element.ownText().matches(pricePattern.pattern());
  }

  @Override
  public int hashCode() {
    return Objects.hash(AnyPriceSelector.class.getName());
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof AnyPriceSelector;
  }

  public static void main(String[] args) {
    AnyPriceSelector selector = new AnyPriceSelector();
  }
}
