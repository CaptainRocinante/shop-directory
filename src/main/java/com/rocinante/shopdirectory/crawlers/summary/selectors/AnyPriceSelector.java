package com.rocinante.shopdirectory.crawlers.summary.selectors;

import com.rocinante.shopdirectory.selectors.ElementNotSelected;
import com.rocinante.shopdirectory.selectors.ElementProperties;
import com.rocinante.shopdirectory.selectors.ElementSelector;
import io.vavr.control.Either;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import org.jsoup.nodes.Element;

public class AnyPriceSelector implements ElementSelector {
  private static final String MONEY_OBJECT_PROPERTY = "money";
  private static final Pattern PRICE_PATTERN = Pattern.compile("(USD|EUR|€|\\$|£)\\s*(\\d{1,3}(?:["
      + ".,]\\d{3})*(?:[.,]\\d{2})?)|(\\d{1,3}(?:[.,]\\d{3})*(?:[.,]\\d{2})?)\\s*?(USD|EUR|€|\\$|£)");

  @Override
  public Either<ElementNotSelected, ElementProperties> select(Element element) {
    if (!element.ownText().matches(PRICE_PATTERN.pattern())) {
      return Either.left(ElementNotSelected.getInstance());
    } else {
      Map<String, Object> properties = new HashMap<>();
      return Either.right(new ElementProperties(element, properties));
    }
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
