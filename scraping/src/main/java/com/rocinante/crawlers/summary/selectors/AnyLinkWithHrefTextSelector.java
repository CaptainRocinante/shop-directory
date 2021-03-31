package com.rocinante.crawlers.summary.selectors;

import com.rocinante.selectors.NodeNotSelected;
import com.rocinante.selectors.NodeProperties;
import com.rocinante.selectors.NodeSelector;
import com.rocinante.util.HtmlUtils;
import io.vavr.control.Either;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

public class AnyLinkWithHrefTextSelector implements NodeSelector {
  public static final String URL_PROPERTY = "url";
  public static final String TEXT_PROPERTY = "text";

  @Override
  public Either<NodeNotSelected, NodeProperties> select(Node node) {
    if (!(node instanceof Element)) {
      return Either.left(NodeNotSelected.getInstance());
    }
    final Element element = (Element) node;
    if (element.tagName().equals("a")
        && element.hasAttr("href")
        && HtmlUtils.isValidUri(element.attr("abs:href"))) {
      Map<String, Object> properties = new HashMap<>();
      properties.put(URL_PROPERTY, element.attr("abs:href"));
      properties.put(TEXT_PROPERTY, element.text());
      return Either.right(new NodeProperties(element, properties));
    } else {
      return Either.left(NodeNotSelected.getInstance());
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
