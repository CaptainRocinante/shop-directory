package com.rocinante.shopdirectory.crawlers.category;

import com.rocinante.shopdirectory.crawlers.CrawlContext;
import com.rocinante.shopdirectory.crawlers.Crawler;
import com.rocinante.shopdirectory.crawlers.CrawlerType;
import com.rocinante.shopdirectory.crawlers.MapCrawlContext;
import com.rocinante.shopdirectory.html.RenderedHtml;
import com.rocinante.shopdirectory.html.RenderedHtmlProvider;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class CategoryCrawler implements Crawler<List<Category>> {
  private final RenderedHtmlProvider renderedHtmlProvider;

  public CategoryCrawler(RenderedHtmlProvider renderedHtmlProvider) {
    this.renderedHtmlProvider = renderedHtmlProvider;
  }

  private List<Category> bfs(Document document) {
    final CategoryScorer categoryScorer = new CategoryScorer();
    final Queue<CategorySiblings> bfsQueue =
        new LinkedList<>(
            CategorySiblingsFactory.createSiblingsFromChildNodes(
                document.childNodes(), categoryScorer));
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

    final Set<Category> allCategories = new HashSet<>();
    while (!categorySiblingsCategoryScorePriorityQueue.isEmpty()) {
      CategorySiblings current = categorySiblingsCategoryScorePriorityQueue.poll();
      if (current.categoryScore() > 3) {
        allCategories.addAll(
            current.getLinks().stream()
                .map(element -> new Category(element.attr("abs:href"), element.text()))
                .collect(Collectors.toList()));
      }
      current.printAllLinks();
    }
    return new ArrayList<>(allCategories);
  }

  @Override
  public CrawlerType type() {
    return CrawlerType.CATEGORY;
  }

  @Override
  public List<Category> crawlUrl(String url, CrawlContext crawlContext) {
    return crawlHtml(renderedHtmlProvider.downloadHtml(url), url, new MapCrawlContext(null));
  }

  @Override
  public List<Category> crawlHtml(RenderedHtml html, String baseUrl, CrawlContext crawlContext) {
    final Document doc = Jsoup.parse(html.getHtml(), baseUrl);
    return bfs(doc);
  }

  public static void main(String[] args) throws InterruptedException {
    //    Jsoup.parse(new File(
    //            CategoryCrawler.class.getClassLoader().getResource("nestnewyork.html").getFile()),
    //        "utf-8");
    //    Document doc = Jsoup.connect("https://www.dsw.com/").get();

    CategoryCrawler categoryCrawler = new CategoryCrawler(new RenderedHtmlProvider());
    List<Category> categories =
        categoryCrawler.crawlUrl("https://www.adidas.com/", new MapCrawlContext(null));
    categories.forEach(c -> System.out.println(c.toString()));
  }
}
