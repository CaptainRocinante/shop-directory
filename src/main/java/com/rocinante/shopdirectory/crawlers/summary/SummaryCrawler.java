package com.rocinante.shopdirectory.crawlers.summary;

import static com.rocinante.shopdirectory.crawlers.summary.selectors.AnyLinkWithHrefTextSelector.TEXT_PROPERTY;
import static com.rocinante.shopdirectory.crawlers.summary.selectors.AnyLinkWithHrefTextSelector.URL_PROPERTY;
import static com.rocinante.shopdirectory.crawlers.summary.selectors.AnyPriceSelector.LIST_MONEY_OBJECT_PROPERTY;
import static com.rocinante.shopdirectory.crawlers.summary.selectors.ImageSelector.IMAGE_SRC_URL;

import com.rocinante.shopdirectory.crawlers.CrawlContext;
import com.rocinante.shopdirectory.crawlers.Crawler;
import com.rocinante.shopdirectory.crawlers.CrawlerType;
import com.rocinante.shopdirectory.crawlers.MapCrawlContext;
import com.rocinante.shopdirectory.crawlers.summary.selectors.AnyLinkWithHrefTextSelector;
import com.rocinante.shopdirectory.crawlers.summary.selectors.AnyPriceSelector;
import com.rocinante.shopdirectory.crawlers.summary.selectors.ImageSelector;
import com.rocinante.shopdirectory.selectors.ElementProperties;
import com.rocinante.shopdirectory.selectors.ElementSelector;
import com.rocinante.shopdirectory.util.MoneyUtils;
import com.rocinante.shopdirectory.util.RenderedHtmlProvider;
import io.vavr.Tuple2;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;
import org.javamoney.moneta.FastMoney;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

public class SummaryCrawler implements Crawler<List<ProductSummary>> {
  public static final AnyLinkWithHrefTextSelector ANY_LINK_WITH_HREF_SELECTOR =
      new AnyLinkWithHrefTextSelector();
  public static final AnyPriceSelector ANY_PRICE_SELECTOR = new AnyPriceSelector();
  public static final ImageSelector IMAGE_SELECTOR = new ImageSelector();
  public static final ElementSelector[] ALL_SUMMARY_SELECTORS = new ElementSelector[] {
      ANY_LINK_WITH_HREF_SELECTOR,
      ANY_PRICE_SELECTOR,
      IMAGE_SELECTOR
  };

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

    final SubtreeTraversalResult result = new SubtreeTraversalResult(
        root, subtreeTraversalResults, ALL_SUMMARY_SELECTORS);
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

  private Tuple2<FastMoney, FastMoney> getOriginalAndSalePrice(List<ElementProperties> prices) {
    final FastMoney highestAmount =
        prices.stream()
            .map(p -> (List<FastMoney>) p.getProperty(LIST_MONEY_OBJECT_PROPERTY))
            .flatMap(Collection::stream)
            .reduce(MoneyUtils::max)
            .orElseThrow();
    final FastMoney lowestAmount =
        prices.stream()
            .map(p -> (List<FastMoney>) p.getProperty(LIST_MONEY_OBJECT_PROPERTY))
            .flatMap(Collection::stream)
            .reduce(MoneyUtils::min)
            .orElseThrow();
    return new Tuple2<>(highestAmount, lowestAmount);
  }

  @Override
  public List<ProductSummary> crawlHtml(String html, String baseUrl, CrawlContext crawlContext) {
    System.out.println(html);
    final PriorityQueue<SubtreeTraversalResult> highestLcsScoreHeap =
        new PriorityQueue<>((r1, r2) -> r2.getChildrenLCSScore() - r1.getChildrenLCSScore());
    final PriorityQueue<SubtreeTraversalResult> highestChildrenElementSelectorScoreHeap =
        new PriorityQueue<>((r1, r2) ->
            r2.childrenCountMatchingAllSelectors() - r1.childrenCountMatchingAllSelectors());
    dfs(Jsoup.parse(html, baseUrl), highestLcsScoreHeap);
    SubtreeTraversalResult top = highestLcsScoreHeap.poll();
    while (!highestLcsScoreHeap.isEmpty() && top != null && top.getChildrenLCSScore() > 0) {
      if (top.childrenCountMatchingAllSelectors() != 0) {
        highestChildrenElementSelectorScoreHeap.add(top);
      }
      top = highestLcsScoreHeap.poll();
    }

    final SubtreeTraversalResult productsRoot = highestChildrenElementSelectorScoreHeap.peek();

    assert productsRoot != null;
    return productsRoot
        .getChildrenWithAllSelectors()
        .stream()
        .map(SubtreeTraversalResult::getElementSelectionResult)
        .map(esr -> {
          final ElementProperties urlProperties =
              esr.getSelectedProperties(ANY_LINK_WITH_HREF_SELECTOR).get(0);
          final Tuple2<FastMoney, FastMoney> priceProperties =
              getOriginalAndSalePrice(esr.getSelectedProperties(ANY_PRICE_SELECTOR));
          final List<String> imageUrls =
              esr.getSelectedProperties(IMAGE_SELECTOR)
                  .stream()
                  .map(p -> (String) p.getProperty(IMAGE_SRC_URL))
                  .collect(Collectors.toList());

          return new ProductSummary((String) urlProperties.getProperty(URL_PROPERTY),
              (String) urlProperties.getProperty(TEXT_PROPERTY), imageUrls,
              priceProperties._1(), priceProperties._2());
        })
        .collect(Collectors.toList());
  }


  public static void main(String[] args) {
    final SummaryCrawler summaryCrawler = new SummaryCrawler(new RenderedHtmlProvider());
//    List<ProductSummary> productSummaries = summaryCrawler.crawlHtml(
//        ResourceUtils.readFileContents("dswsummarypage.html"),
//        "https://www.dsw.com/", new MapCrawlContext(null));
    List<ProductSummary> productSummaries = summaryCrawler.crawlUrl(
        "https://www.kmart.com.au/category/mens/mens-accessories/mens-wallets/508519#.plp-wrapper",
        new MapCrawlContext(null));
    productSummaries.forEach(ps -> System.out.println(ps.toString()));
  }
}
