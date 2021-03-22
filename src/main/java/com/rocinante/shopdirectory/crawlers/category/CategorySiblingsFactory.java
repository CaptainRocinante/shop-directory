package com.rocinante.shopdirectory.crawlers.category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

public class CategorySiblingsFactory {
  public static List<CategorySiblings> createSiblingsFromChildNodes(List<Node> childNodes,
      CategoryScorer categoryScorer) {
    Map<String, CategorySiblings> sameTagSiblings = new HashMap<>();
    childNodes
        .stream()
        .filter(x -> x instanceof Element)
        .map(x -> (Element) x)
        .forEach(
            element -> {
              String elementTag = element.tag().getName();
              if (sameTagSiblings.containsKey(elementTag)) {
                sameTagSiblings.get(elementTag).addElement(element);
              } else {
                CategorySiblings categorySiblings = new CategorySiblings(categoryScorer);
                categorySiblings.addElement(element);
                sameTagSiblings.put(elementTag, categorySiblings);
              }
            });
    return new ArrayList<>(sameTagSiblings.values());
  }
}
