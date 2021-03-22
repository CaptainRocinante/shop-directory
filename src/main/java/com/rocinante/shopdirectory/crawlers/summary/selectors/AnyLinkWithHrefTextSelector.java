package com.rocinante.shopdirectory.crawlers.summary.selectors;

import com.rocinante.shopdirectory.selectors.ElementNotSelected;
import com.rocinante.shopdirectory.selectors.ElementProperties;
import com.rocinante.shopdirectory.selectors.ElementSelector;
import io.vavr.control.Either;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.jsoup.nodes.Element;

public class AnyLinkWithHrefTextSelector implements ElementSelector {
  public static final String URL_PROPERTY = "url";
  public static final String TEXT_PROPERTY = "text";

  @Override
  public Either<ElementNotSelected, ElementProperties> select(Element element) {
    if(element.tagName().equals("a") && element.hasAttr("href")
        && !element.text().isBlank()) {
      Map<String, Object> properties = new HashMap<>();
      properties.put(URL_PROPERTY, element.attr("abs:href"));
      properties.put(TEXT_PROPERTY, element.text());
      return Either.right(new ElementProperties(element, properties));
    } else {
      return Either.left(ElementNotSelected.getInstance());
    }
  }

  @Override
  public int hashCode() {
    return Objects.hash(AnyLinkWithHrefTextSelector.class.getName());
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof AnyLinkWithHrefTextSelector;
  }
}
