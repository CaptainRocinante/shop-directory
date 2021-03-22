package com.rocinante.shopdirectory.selectors;

import io.vavr.control.Either;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.jsoup.nodes.Element;

public class ElementSelectionResult {
  private final Map<ElementSelector, List<ElementProperties>> elementSelectorMap;

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

    for (final Map.Entry<ElementSelector, List<ElementProperties>> e : other.elementSelectorMap.entrySet()) {
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
      final Either<ElementNotSelected, ElementProperties> selectionResult =
          elementSelector.select(element);
      if (selectionResult.isRight()) {
        elementSelectorMap.get(elementSelector).add(selectionResult.get());
      }
    });
  }

  public boolean containsSelectorItems(ElementSelector elementSelector) {
    if (!elementSelectorMap.containsKey(elementSelector)) {
      return false;
    } else {
      return !elementSelectorMap.get(elementSelector).isEmpty();
    }
  }
}
