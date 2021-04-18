package com.rocinante.scraping.selectors;

import java.util.Map;
import org.jsoup.nodes.Node;

public class NodeProperties {
  private final Node node;
  private Map<String, Object> selectedProperties;

  public NodeProperties(Node node, Map<String, Object> selectedProperties) {
    this.node = node;
    this.selectedProperties = selectedProperties;
  }

  public Object getProperty(String key) {
    return selectedProperties.get(key);
  }
}
