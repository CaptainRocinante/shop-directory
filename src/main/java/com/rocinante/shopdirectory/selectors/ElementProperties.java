package com.rocinante.shopdirectory.selectors;

import java.util.Map;
import org.jsoup.nodes.Element;

public class ElementProperties {
  private final Element element;
  private Map<String, Object> selectedProperties;

  public ElementProperties(Element element,
      Map<String, Object> selectedProperties) {
    this.element = element;
    this.selectedProperties = selectedProperties;
  }

  public Object getProperty(String key) {
    return selectedProperties.get(key);
  }
}
