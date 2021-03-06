package com.rocinante.scraping.selectors;

import io.vavr.control.Either;
import org.jsoup.nodes.Node;

public interface NodeSelector {
  Either<NodeNotSelected, NodeProperties> select(Node node);
}
