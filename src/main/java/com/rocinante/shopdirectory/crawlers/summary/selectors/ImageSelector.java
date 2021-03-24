package com.rocinante.shopdirectory.crawlers.summary.selectors;

import com.rocinante.shopdirectory.selectors.NodeNotSelected;
import com.rocinante.shopdirectory.selectors.NodeProperties;
import com.rocinante.shopdirectory.selectors.NodeSelector;
import io.vavr.control.Either;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

public class ImageSelector implements NodeSelector {
  public static final String IMAGE_SRC_URL = "image_url";

  @Override
  public Either<NodeNotSelected, NodeProperties> select(Node node) {
    if (!(node instanceof Element)) {
      return Either.left(NodeNotSelected.getInstance());
    }
    final Element element = (Element) node;
    if (element.tagName().equals("img")) {
      Map<String, Object> properties = new HashMap<>();
      properties.put(IMAGE_SRC_URL, element.attr("abs:src"));
      return Either.right(new NodeProperties(element, properties));
    } else {
      return Either.left(NodeNotSelected.getInstance());
    }
  }

  @Override
  public int hashCode() {
    return Objects.hash(ImageSelector.class.getName());
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof ImageSelector;
  }
}
