package com.rocinante.crawlers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

class Siblings {
  private final CategoryScorer categoryScorer;
  private final List<Element> siblings;

  public Siblings(CategoryScorer categoryScorer) {
    this.siblings = new ArrayList<>();
    this.categoryScorer = categoryScorer;
  }

  public void addElement(Element e) {
    this.siblings.add(e);
  }

  public List<Element> getLinks() {
    return siblings
        .stream()
        .filter(e -> e.tag().getName().equals("a"))
        .collect(Collectors.toList());
  }

  public void printAllLinks() {
    if (getLinks().size() != 0) {
      System.out.printf(
          "\n-------------------\nPrinting a set of Sibling links\n-------------------\nScore = "
              + "%d\n", categoryScore());
    }
    getLinks().forEach(l -> System.out.println("Link: " + l));
  }

  public List<Siblings> getChildren() {
    return SiblingsFactory.createSiblingsFromChildNodes(
        siblings
            .stream()
            .map(Node::childNodes)
            .flatMap(List::stream)
            .collect(Collectors.toList()), categoryScorer);
  }

  public int categoryScore() {
    return categoryScorer.score(this);
  }
}
