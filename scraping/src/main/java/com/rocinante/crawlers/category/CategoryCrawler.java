package com.rocinante.crawlers.category;

import com.rocinante.crawlers.CrawlContext;
import com.rocinante.crawlers.Crawler;
import com.rocinante.crawlers.CrawlerType;
import com.rocinante.crawlers.MapCrawlContext;
import com.rocinante.html.RenderedHtml;
import com.rocinante.html.RenderedHtmlProvider;
import com.rocinante.util.UrlUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

@Slf4j
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

    while (!bfsQueue.isEmpty()) {
      int currentLevelCount = bfsQueue.size();

      for (int i = 0; i < currentLevelCount; ++i) {
        CategorySiblings current = bfsQueue.poll();
        categorySiblingsCategoryScorePriorityQueue.add(current);
        bfsQueue.addAll(current.getChildren());
      }
    }

    final Set<Category> allCategories = new HashSet<>();
    while (!categorySiblingsCategoryScorePriorityQueue.isEmpty()) {
      CategorySiblings current = categorySiblingsCategoryScorePriorityQueue.poll();
      if (current.categoryScore() > 3) {
        allCategories.addAll(
            current.getLinks().stream()
                .filter(e -> UrlUtils.isValidUri(e.attr("abs:href")))
                .map(element -> new Category(element.attr("abs:href"), element.text()))
                .collect(Collectors.toList()));
      }
      //      current.printAllLinks();
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

  public static void main(String[] args) {
    //    Jsoup.parse(new File(
    //            CategoryCrawler.class.getClassLoader().getResource("nestnewyork.html").getFile()),
    //        "utf-8");
    //    Document doc = Jsoup.connect("https://www.dsw.com/").get();

    CategoryCrawler categoryCrawler =
        new CategoryCrawler(
            new RenderedHtmlProvider(true,
                "http://127.0.0.1:8888", "/usr/local/bin/chromedriver"));
    List<Category> categories =
        categoryCrawler.crawlUrl("https://www.adidas.com/", new MapCrawlContext(null));
    categories.forEach(c -> log.info("Category {}", c.toString()));
  }
}
