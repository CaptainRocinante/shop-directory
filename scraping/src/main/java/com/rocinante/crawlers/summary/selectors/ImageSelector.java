package com.rocinante.crawlers.summary.selectors;

import com.rocinante.selectors.NodeNotSelected;
import com.rocinante.selectors.NodeProperties;
import com.rocinante.selectors.NodeSelector;
import com.rocinante.util.HtmlUtils;
import io.vavr.control.Either;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

public class ImageSelector implements NodeSelector {
  public static final String IMAGE_SRC_URLS = "image_urls";

  @Override
  public Either<NodeNotSelected, NodeProperties> select(Node node) {
    if (!(node instanceof Element)) {
      return Either.left(NodeNotSelected.getInstance());
    }
    final Element element = (Element) node;
    if (element.tagName().equals("img")) {
      final Map<String, Object> properties = new HashMap<>();
      final List<String> imageUrls = new LinkedList<>();
      final String src = element.attr("abs:src");
      final String srcSet = element.attr("abs:srcset");
      if (!src.isBlank()) {
        imageUrls.add(src);
      }
      if (!srcSet.isBlank()) {
        imageUrls.add(HtmlUtils.extractImageUrlsFromSrcSet(srcSet).get(0));
      }
      properties.put(IMAGE_SRC_URLS, imageUrls);
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
