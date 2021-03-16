package com.rocinante.crawlers;

import java.io.IOException;
import java.util.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class CategoryCrawler {
  private void bfs(Document document) {
    final CategoryScorer categoryScorer = new CategoryScorer();
    final Queue<Siblings> bfsQueue =
        new LinkedList<>(
            SiblingsFactory.createSiblingsFromChildNodes(document.childNodes(), categoryScorer));
    final PriorityQueue<Siblings> siblingsCategoryScorePriorityQueue = new PriorityQueue<>(
        (o1, o2) -> o2.categoryScore() - o1.categoryScore());

    int level = 0;
    while (!bfsQueue.isEmpty()) {
      int currentLevelCount = bfsQueue.size();

      System.out.println("List for level " + level + " of size " + currentLevelCount);

      for (int i = 0; i < currentLevelCount; ++i) {
        Siblings current = bfsQueue.poll();
        siblingsCategoryScorePriorityQueue.add(current);
        bfsQueue.addAll(current.getChildren());
      }
      ++level;
    }

    while (!siblingsCategoryScorePriorityQueue.isEmpty()) {
      Siblings current = siblingsCategoryScorePriorityQueue.poll();
      current.printAllLinks();
    }
  }

  public static void main(String[] args) throws IOException {
//    Document doc =
//        Jsoup.parse(
//            new File(CategoryCrawler.class.getClassLoader().getResource("pacha.html").getFile()),
//            "utf-8");
    Document doc = Jsoup.connect("https://www.aritzia.com/").get();
    CategoryCrawler categoryCrawler = new CategoryCrawler();
    categoryCrawler.bfs(doc);
  }
}
