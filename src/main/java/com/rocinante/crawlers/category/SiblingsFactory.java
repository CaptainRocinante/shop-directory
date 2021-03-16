package com.rocinante.crawlers.category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

public class SiblingsFactory {
  public static List<Siblings> createSiblingsFromChildNodes(List<Node> childNodes,
      CategoryScorer categoryScorer) {
    Map<String, Siblings> sameTagSiblings = new HashMap<>();
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
                Siblings siblings = new Siblings(categoryScorer);
                siblings.addElement(element);
                sameTagSiblings.put(elementTag, siblings);
              }
            });
    return new ArrayList<>(sameTagSiblings.values());
  }
}
