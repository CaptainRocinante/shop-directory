package com.rocinante.scraping.crawlers.category;

import com.rocinante.common.api.crawlers.Category;
import com.rocinante.scraping.crawlers.CrawlContext;
import com.rocinante.scraping.crawlers.Crawler;
import com.rocinante.scraping.crawlers.CrawlerType;
import com.rocinante.scraping.crawlers.MapCrawlContext;
import com.rocinante.scraping.html.RenderedHtml;
import com.rocinante.scraping.html.RenderedHtmlProvider;
import com.rocinante.scraping.util.UrlUtils;
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
import org.springframework.stereotype.Component;

@Slf4j
@Component
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
    //    log.info(html.getHtml());
    final Document doc = Jsoup.parse(html.getHtml(), baseUrl);
    return bfs(doc);
  }

  /*
   * Demo List
   *
   * https://www.untuckit.com/
   * https://bdgastore.com/
   * https://www.notre-shop.com/
   *
   */
  public static void main(String[] args) {
    CategoryCrawler categoryCrawler =
        new CategoryCrawler(
            new RenderedHtmlProvider(
                true,
                "http://ec2-18-209-34-13.compute-1.amazonaws.com:8888",
                "/usr/local/bin/chromedriver"));
    List<Category> categories =
        categoryCrawler.crawlUrl("https://www.notre-shop.com/", new MapCrawlContext(null));
    categories.forEach(c -> log.info("Category {}", c.toString()));
  }
}
