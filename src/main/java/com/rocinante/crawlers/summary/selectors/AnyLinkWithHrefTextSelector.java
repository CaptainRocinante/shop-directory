package com.rocinante.crawlers.summary.selectors;

import com.rocinante.crawlers.infrastructure.ElementSelector;
import java.util.Objects;
import org.jsoup.nodes.Element;

public class AnyLinkWithHrefTextSelector implements ElementSelector {
  @Override
  public boolean select(Element element) {
    return element.tagName().equals("a") && element.hasAttr("href")
        && !element.text().isBlank();
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
