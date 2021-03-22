package com.rocinante.crawlers.summary.selectors;

import com.rocinante.crawlers.infrastructure.ElementSelector;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jsoup.nodes.Element;

public class ElementSelectionResult {
  private final Map<ElementSelector, List<Element>> elementSelectorMap;

  public ElementSelectionResult() {
    this.elementSelectorMap = new HashMap<>();
  }

  public ElementSelectionResult(ElementSelector... selectorsToTest) {
    this.elementSelectorMap = new HashMap<>();
    Arrays.stream(selectorsToTest).forEach(sl -> elementSelectorMap.put(sl, new LinkedList<>()));
  }

  public ElementSelectionResult merge(ElementSelectionResult other) {
    final ElementSelectionResult elementSelectionResult = new ElementSelectionResult();
    elementSelectionResult.elementSelectorMap.putAll(this.elementSelectorMap);

    for (final Map.Entry<ElementSelector, List<Element>> e : other.elementSelectorMap.entrySet()) {
      if (elementSelectionResult.elementSelectorMap.containsKey(e.getKey())) {
        elementSelectionResult.elementSelectorMap.get(e.getKey()).addAll(e.getValue());
      } else {
        elementSelectionResult.elementSelectorMap.put(e.getKey(), e.getValue());
      }
    }
    return elementSelectionResult;
  }

  public void addElementToAllSelectorsMatched(Element element) {
    elementSelectorMap.keySet().forEach(elementSelector -> {
      if (elementSelector.select(element)) {
        elementSelectorMap.get(elementSelector).add(element);
      }
    });
  }
}
