package com.rocinante.shopdirectory.crawlers.summary.selectors;

import com.rocinante.shopdirectory.selectors.NodeNotSelected;
import com.rocinante.shopdirectory.selectors.NodeProperties;
import com.rocinante.shopdirectory.selectors.NodeSelector;
import io.vavr.control.Either;
import java.util.HashMap;
import java.util.Map;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

public class TextNodeSelector implements NodeSelector {
  public static final String OWN_TEXT_PROPERTY = "own_text";

  @Override
  public Either<NodeNotSelected, NodeProperties> select(Node node) {
    if (node instanceof TextNode) {
      final TextNode textNode = (TextNode) node;
      final Map<String, Object> properties = new HashMap<>();
      final String ownText = textNode.text();
      if (ownText.isBlank()) {
        return Either.left(NodeNotSelected.getInstance());
      }
      properties.put(OWN_TEXT_PROPERTY, ownText);
      return Either.right(new NodeProperties(textNode, properties));
    } else {
      return Either.left(NodeNotSelected.getInstance());
    }
  }
}
