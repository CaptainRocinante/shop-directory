package com.rocinante.shopdirectory.crawlers.summary;

import static com.rocinante.shopdirectory.crawlers.summary.selectors.AnyLinkWithHrefTextSelector.TEXT_PROPERTY;
import static com.rocinante.shopdirectory.crawlers.summary.selectors.AnyLinkWithHrefTextSelector.URL_PROPERTY;
import static com.rocinante.shopdirectory.crawlers.summary.selectors.AnyPriceSelector.LIST_MONEY_OBJECT_PROPERTY;
import static com.rocinante.shopdirectory.crawlers.summary.selectors.ImageSelector.IMAGE_SRC_URL;
import static com.rocinante.shopdirectory.crawlers.summary.selectors.TextNodeSelector.OWN_TEXT_PROPERTY;

import com.rocinante.shopdirectory.crawlers.CrawlContext;
import com.rocinante.shopdirectory.crawlers.Crawler;
import com.rocinante.shopdirectory.crawlers.CrawlerType;
import com.rocinante.shopdirectory.crawlers.MapCrawlContext;
import com.rocinante.shopdirectory.crawlers.category.CategoryScorer;
import com.rocinante.shopdirectory.crawlers.summary.selectors.AnyLinkWithHrefTextSelector;
import com.rocinante.shopdirectory.crawlers.summary.selectors.AnyPriceSelector;
import com.rocinante.shopdirectory.crawlers.summary.selectors.ImageSelector;
import com.rocinante.shopdirectory.crawlers.summary.selectors.TextNodeSelector;
import com.rocinante.shopdirectory.lcs.LongestCommonSubsequence;
import com.rocinante.shopdirectory.lcs.StringLCSToken;
import com.rocinante.shopdirectory.selectors.NodeProperties;
import com.rocinante.shopdirectory.selectors.NodeSelector;
import com.rocinante.shopdirectory.util.MoneyUtils;
import com.rocinante.shopdirectory.util.RenderedHtmlProvider;
import com.rocinante.shopdirectory.util.ResourceUtils;
import com.rocinante.shopdirectory.util.Tokenizer;
import io.vavr.Tuple2;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import org.javamoney.moneta.FastMoney;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Node;

public class SummaryCrawler implements Crawler<List<ProductSummary>> {
  public static final AnyLinkWithHrefTextSelector ANY_LINK_WITH_HREF_SELECTOR =
      new AnyLinkWithHrefTextSelector();
  public static final AnyPriceSelector ANY_PRICE_SELECTOR = new AnyPriceSelector();
  public static final ImageSelector IMAGE_SELECTOR = new ImageSelector();
  public static final TextNodeSelector TEXT_NODE_SELECTOR = new TextNodeSelector();
  public static final NodeSelector[] ALL_SUMMARY_SELECTORS = new NodeSelector[] {
      ANY_LINK_WITH_HREF_SELECTOR,
      ANY_PRICE_SELECTOR,
      IMAGE_SELECTOR,
      TEXT_NODE_SELECTOR
  };

  private final RenderedHtmlProvider renderedHtmlProvider;
  private final CategoryScorer categoryScorer;

  public SummaryCrawler(RenderedHtmlProvider renderedHtmlProvider) {
    this.renderedHtmlProvider = renderedHtmlProvider;
    this.categoryScorer = new CategoryScorer();
  }

  private SubtreeTraversalResult dfs(Node root,
      PriorityQueue<SubtreeTraversalResult> highestLcsScoreHeap) {
    final List<SubtreeTraversalResult> subtreeTraversalResults = new LinkedList<>();

    root
        .childNodes()
        .forEach(node -> subtreeTraversalResults.add(dfs(node, highestLcsScoreHeap)));

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

  private Tuple2<FastMoney, FastMoney> getOriginalAndSalePrice(List<NodeProperties> prices) {
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

  private String getProductTitle(List<NodeProperties> allTextNodesSelected,
      NodeProperties urlProperties) {
    final String url = (String) urlProperties.getProperty(URL_PROPERTY);
    final String urlText = (String) urlProperties.getProperty(TEXT_PROPERTY);

    final List<StringLCSToken> urlTokens = Tokenizer.alphaNumericLcsTokens(url);

    final AtomicInteger maxLcsScore = new AtomicInteger(Integer.MIN_VALUE);
    final AtomicInteger maxCategoryScore = new AtomicInteger(Integer.MIN_VALUE);
    final AtomicReference<String> maxLcsScoreText = new AtomicReference<>(null);
    final AtomicReference<String> maxCategoryScoreText = new AtomicReference<>(null);

    allTextNodesSelected
        .stream()
        .map(np -> (String) np.getProperty(OWN_TEXT_PROPERTY))
        .forEach(text -> {
          final List<StringLCSToken> textTokens = Tokenizer.alphaNumericLcsTokens(text);
          final int lcsScore = LongestCommonSubsequence
              .computeLcsStringTokensOnly(textTokens, urlTokens);
          final int categoryScore = categoryScorer.scoreText(text);
          if (lcsScore != 0 && lcsScore > maxLcsScore.get()) {
            maxLcsScore.set(lcsScore);
            maxLcsScoreText.set(text);
          }
          if (categoryScore != 0 && categoryScore > maxCategoryScore.get()) {
            maxCategoryScore.set(categoryScore);
            maxCategoryScoreText.set(text);
          }
        });
    return maxLcsScoreText.get() != null ? maxLcsScoreText.get() :
        (maxCategoryScoreText.get() != null ? maxCategoryScoreText.get() : urlText);
  }

  @Override
  public List<ProductSummary> crawlHtml(String html, String baseUrl, CrawlContext crawlContext) {
    System.out.println(html);
    final PriorityQueue<SubtreeTraversalResult> highestLcsScoreHeap =
        new PriorityQueue<>((r1, r2) -> r2.getChildrenLCSScore() - r1.getChildrenLCSScore());
    final List<SubtreeTraversalResult> productRoots = new LinkedList<>();
    dfs(Jsoup.parse(html, baseUrl), highestLcsScoreHeap);
    SubtreeTraversalResult top = highestLcsScoreHeap.poll();
    while (!highestLcsScoreHeap.isEmpty() && top != null && top.getChildrenLCSScore() > 0) {
      if (top.childrenCountMatchingAllSelectors() > 2) {
        productRoots.add(top);
      }
      top = highestLcsScoreHeap.poll();
    }

    final List<ProductSummary> results = new ArrayList<>();

    productRoots.forEach(productRoot ->
        results.addAll(
            productRoot
                .getChildrenWithAllSelectors()
                .stream()
                .map(SubtreeTraversalResult::getNodeSelectionResult)
                .map(esr -> {
                  final NodeProperties urlProperties =
                      esr.getSelectedProperties(ANY_LINK_WITH_HREF_SELECTOR).get(0);
                  final Tuple2<FastMoney, FastMoney> priceProperties =
                      getOriginalAndSalePrice(esr.getSelectedProperties(ANY_PRICE_SELECTOR));
                  final List<String> imageUrls =
                      esr.getSelectedProperties(IMAGE_SELECTOR)
                          .stream()
                          .map(p -> (String) p.getProperty(IMAGE_SRC_URL))
                          .collect(Collectors.toList());
                  final List<NodeProperties> textNodeProperties =
                      esr.getSelectedProperties(TEXT_NODE_SELECTOR);
                  final String productTitle = getProductTitle(textNodeProperties, urlProperties);
                  return new ProductSummary((String) urlProperties.getProperty(URL_PROPERTY),
                      productTitle, imageUrls, priceProperties._1(), priceProperties._2());
                })
                .collect(Collectors.toList())));
    return results;
  }


  public static void main(String[] args) {
    final SummaryCrawler summaryCrawler = new SummaryCrawler(new RenderedHtmlProvider());
//    List<ProductSummary> productSummaries = summaryCrawler.crawlHtml(
//        ResourceUtils.readFileContents("dswdummy.html"),
//        "https://www.chubbiesshorts.com/", new MapCrawlContext(null));
    List<ProductSummary> productSummaries = summaryCrawler.crawlUrl(
        "https://supergoop.com/collections/makeup",
        new MapCrawlContext(null));
    productSummaries.forEach(ps -> System.out.println(ps.toString()));
  }
}
