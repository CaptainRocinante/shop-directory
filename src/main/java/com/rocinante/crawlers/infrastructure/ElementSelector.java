package com.rocinante.crawlers.infrastructure;

import org.jsoup.nodes.Element;

public interface ElementSelector {
  boolean select(Element element);
}
