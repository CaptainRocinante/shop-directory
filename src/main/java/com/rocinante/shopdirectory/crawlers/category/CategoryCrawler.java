package com.rocinante.shopdirectory.crawlers.category;

import com.rocinante.shopdirectory.util.RenderedHtmlProvider;
import java.util.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class CategoryCrawler {
  private void bfs(Document document) {
    final CategoryScorer categoryScorer = new CategoryScorer();
    final Queue<CategorySiblings> bfsQueue =
        new LinkedList<>(
            CategorySiblingsFactory
                .createSiblingsFromChildNodes(document.childNodes(), categoryScorer));
    final PriorityQueue<CategorySiblings> categorySiblingsCategoryScorePriorityQueue =
        new PriorityQueue<>((o1, o2) -> o2.categoryScore() - o1.categoryScore());

    int level = 0;
    while (!bfsQueue.isEmpty()) {
      int currentLevelCount = bfsQueue.size();

      System.out.println("List for level " + level + " of size " + currentLevelCount);

      for (int i = 0; i < currentLevelCount; ++i) {
        CategorySiblings current = bfsQueue.poll();
        categorySiblingsCategoryScorePriorityQueue.add(current);
        bfsQueue.addAll(current.getChildren());
      }
      ++level;
    }

    while (!categorySiblingsCategoryScorePriorityQueue.isEmpty()) {
      CategorySiblings current = categorySiblingsCategoryScorePriorityQueue.poll();
      current.printAllLinks();
    }
  }



  public static void main(String[] args) throws InterruptedException {
//    Jsoup.parse(new File(
//            CategoryCrawler.class.getClassLoader().getResource("nestnewyork.html").getFile()),
//        "utf-8");
//    Document doc = Jsoup.connect("https://www.dsw.com/").get();

    final RenderedHtmlProvider renderedHtmlProvider = new RenderedHtmlProvider();
    final String pageSource = renderedHtmlProvider.downloadHtml("https://www.dsw.com/");
    final Document doc = Jsoup.parse(pageSource);
    CategoryCrawler categoryCrawler = new CategoryCrawler();
    categoryCrawler.bfs(doc);
  }
}
