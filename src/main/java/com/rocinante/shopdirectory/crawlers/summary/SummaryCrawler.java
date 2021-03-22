package com.rocinante.shopdirectory.crawlers.summary;

import static com.rocinante.shopdirectory.crawlers.summary.SubtreeTraversalResult.ANY_LINK_WITH_HREF_SELECTOR;
import static com.rocinante.shopdirectory.crawlers.summary.SubtreeTraversalResult.ANY_PRICE_SELECTOR;

import com.rocinante.shopdirectory.crawlers.CrawlContext;
import com.rocinante.shopdirectory.crawlers.Crawler;
import com.rocinante.shopdirectory.crawlers.CrawlerType;
import com.rocinante.shopdirectory.crawlers.MapCrawlContext;
import com.rocinante.shopdirectory.util.RenderedHtmlProvider;
import com.rocinante.shopdirectory.util.ResourceUtils;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

public class SummaryCrawler implements Crawler<List<ProductSummary>> {
  private final RenderedHtmlProvider renderedHtmlProvider;

  public SummaryCrawler(RenderedHtmlProvider renderedHtmlProvider) {
    this.renderedHtmlProvider = renderedHtmlProvider;
  }

  private SubtreeTraversalResult dfs(Element root,
      PriorityQueue<SubtreeTraversalResult> highestLcsScoreHeap) {
    final List<SubtreeTraversalResult> subtreeTraversalResults = new LinkedList<>();

    root
        .childNodes()
        .stream()
        .filter(node -> node instanceof Element)
        .forEach(node -> subtreeTraversalResults.add(dfs((Element) node, highestLcsScoreHeap)));

    final SubtreeTraversalResult result = new SubtreeTraversalResult(root, subtreeTraversalResults);
    highestLcsScoreHeap.add(result);
    return result;
  }

  @Override
  public CrawlerType type() {
    return CrawlerType.SUMMARY;
  }

  @Override
  public List<ProductSummary> crawlUrl(String url, CrawlContext crawlContext) {
    return crawlHtml(renderedHtmlProvider.downloadHtml(url), url, new MapCrawlContext(null));
  }

  @Override
  public List<ProductSummary> crawlHtml(String html, String baseUrl, CrawlContext crawlContext) {
    final PriorityQueue<SubtreeTraversalResult> highestLcsScoreHeap =
        new PriorityQueue<>((r1, r2) -> r2.getChildrenLCSScore() - r1.getChildrenLCSScore());
    final SubtreeTraversalResult subtreeTraversalResult = dfs(Jsoup.parse(html, baseUrl),
        highestLcsScoreHeap);
    List<SubtreeTraversalResult> topHeapItems = new LinkedList<>();
    SubtreeTraversalResult top = highestLcsScoreHeap.poll();
    while (!highestLcsScoreHeap.isEmpty() && top != null && top.getChildrenLCSScore() > 0) {
      if (top.getElementSelectionResult().containsSelectorItems(ANY_LINK_WITH_HREF_SELECTOR) &&
          top.getElementSelectionResult().containsSelectorItems(ANY_PRICE_SELECTOR)) {
        topHeapItems.add(top);
      }
      top = highestLcsScoreHeap.poll();
    }
    return null;
  }


  public static void main(String[] args) throws InterruptedException, IOException {
//    Document doc = Jsoup.parse(new File(
//            SummaryCrawler.class.getClassLoader().getResource("dswsummarypage.html").getFile()),
//        "utf-8", "https://www.dsw.com/en/us/brands/adidas/N-1z141hg");
//    Document doc = Jsoup.connect("https://www.dsw.com/").get();

//    final RenderedHtmlProvider renderedHtmlProvider = new RenderedHtmlProvider();
//    final String pageSource = renderedHtmlProvider.downloadHtml("https://www.dsw"
//        + ".com/en/us/brands/adidas/N-1z141hg");
//    System.out.println(pageSource);
//    final Document doc = Jsoup.parse(pageSource);

    SummaryCrawler summaryCrawler = new SummaryCrawler(new RenderedHtmlProvider());
    summaryCrawler.crawlHtml(
        ResourceUtils.readFileContents("dswsummarypage.html"),
        "https://www.dsw.com/", new MapCrawlContext(null));
  }
}
