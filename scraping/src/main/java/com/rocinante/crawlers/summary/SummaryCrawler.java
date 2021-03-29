package com.rocinante.crawlers.summary;

import static com.rocinante.crawlers.summary.selectors.AnyLinkWithHrefTextSelector.TEXT_PROPERTY;
import static com.rocinante.crawlers.summary.selectors.AnyLinkWithHrefTextSelector.URL_PROPERTY;
import static com.rocinante.crawlers.summary.selectors.AnyPriceRangeSelector.RANGE_MONEY_OBJECT_PROPERTY;
import static com.rocinante.crawlers.summary.selectors.AnyPriceSelector.LIST_MONEY_OBJECT_PROPERTY;

import com.google.common.collect.Range;
import com.rocinante.crawlers.summary.selectors.ImageSelector;
import com.rocinante.crawlers.summary.selectors.TextNodeSelector;
import com.rocinante.html.RenderedHtml;
import com.rocinante.lcs.LongestCommonSubsequence;
import com.rocinante.selectors.NodeProperties;
import com.rocinante.crawlers.CrawlContext;
import com.rocinante.crawlers.Crawler;
import com.rocinante.crawlers.CrawlerType;
import com.rocinante.crawlers.MapCrawlContext;
import com.rocinante.crawlers.category.CategoryScorer;
import com.rocinante.crawlers.summary.selectors.AnyLinkWithHrefTextSelector;
import com.rocinante.crawlers.summary.selectors.AnyPriceRangeSelector;
import com.rocinante.crawlers.summary.selectors.AnyPriceSelector;
import com.rocinante.html.RenderedHtmlProvider;
import com.rocinante.lcs.StringLCSToken;
import com.rocinante.selectors.NodeSelector;
import com.rocinante.util.HtmlUtils;
import com.rocinante.util.MoneyUtils;
import com.rocinante.util.Tokenizer;
import io.vavr.Tuple2;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.javamoney.moneta.FastMoney;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Node;

@Slf4j
public class SummaryCrawler implements Crawler<List<ProductSummary>> {
  public static final AnyLinkWithHrefTextSelector ANY_LINK_WITH_HREF_SELECTOR =
      new AnyLinkWithHrefTextSelector();
  public static final AnyPriceSelector ANY_PRICE_SELECTOR = new AnyPriceSelector();
  public static final ImageSelector IMAGE_SELECTOR = new ImageSelector();
  public static final TextNodeSelector TEXT_NODE_SELECTOR = new TextNodeSelector();
  public static final AnyPriceRangeSelector ANY_PRICE_RANGE_SELECTOR = new AnyPriceRangeSelector();
  public static final NodeSelector[] ALL_SUMMARY_REQUIRED_SELECTORS =
      new NodeSelector[] {
        ANY_LINK_WITH_HREF_SELECTOR, ANY_PRICE_SELECTOR, IMAGE_SELECTOR, TEXT_NODE_SELECTOR
      };
  public static final NodeSelector[] ALL_SUMMARY_OPTIONAL_SELECTORS =
      new NodeSelector[] {ANY_PRICE_RANGE_SELECTOR};

  private final RenderedHtmlProvider renderedHtmlProvider;
  private final CategoryScorer categoryScorer;

  public SummaryCrawler(RenderedHtmlProvider renderedHtmlProvider) {
    this.renderedHtmlProvider = renderedHtmlProvider;
    this.categoryScorer = new CategoryScorer();
  }

  private SubtreeTraversalResult dfs(
      Node root, PriorityQueue<SubtreeTraversalResult> highestLcsScoreHeap) {
    final List<SubtreeTraversalResult> subtreeTraversalResults = new LinkedList<>();

    root.childNodes().forEach(node -> subtreeTraversalResults.add(dfs(node, highestLcsScoreHeap)));

    final SubtreeTraversalResult result =
        new SubtreeTraversalResult(
            root,
            subtreeTraversalResults,
            ALL_SUMMARY_REQUIRED_SELECTORS,
            ALL_SUMMARY_OPTIONAL_SELECTORS);
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

  private Tuple2<Range<FastMoney>, Range<FastMoney>> getOriginalAndSalePrice(
      List<NodeProperties> prices, List<NodeProperties> priceRanges) {
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
    if (priceRanges.isEmpty()) {
      return new Tuple2<>(
          Range.singleton(highestAmount),
          lowestAmount.isLessThan(highestAmount) ? Range.singleton(lowestAmount) : null);
    } else {
      // Just picking 1 range for now, don't think there would be a product with multiple price
      // range listings
      final Range<FastMoney> priceRange =
          (Range<FastMoney>) priceRanges.get(0).getProperty(RANGE_MONEY_OBJECT_PROPERTY);
      if (highestAmount.isGreaterThan(priceRange.upperEndpoint())) {
        return new Tuple2<>(Range.singleton(highestAmount), priceRange);
      } else if (lowestAmount.isLessThan(priceRange.lowerEndpoint())) {
        return new Tuple2<>(priceRange, Range.singleton(lowestAmount));
      } else {
        return new Tuple2<>(priceRange, null);
      }
    }
  }

  private String getProductTitle(
      List<NodeProperties> allTextNodesSelected, NodeProperties urlProperties) {
    final String url = (String) urlProperties.getProperty(URL_PROPERTY);
    final String urlText = (String) urlProperties.getProperty(TEXT_PROPERTY);

    final List<StringLCSToken> urlTokens = Tokenizer.alphaNumericLcsTokens(url);

    final AtomicInteger maxLcsScore = new AtomicInteger(Integer.MIN_VALUE);
    final AtomicInteger maxCategoryScore = new AtomicInteger(Integer.MIN_VALUE);
    final AtomicReference<String> maxLcsScoreText = new AtomicReference<>(null);
    final AtomicReference<String> maxCategoryScoreText = new AtomicReference<>(null);

    allTextNodesSelected.stream()
        .map(np -> (String) np.getProperty(TextNodeSelector.OWN_TEXT_PROPERTY))
        .distinct()
        .forEach(
            text -> {
              final List<StringLCSToken> textTokens = Tokenizer.alphaNumericLcsTokens(text);
              final int lcsScore =
                  LongestCommonSubsequence.computeLcsStringTokensOnly(textTokens, urlTokens);
              final int categoryScore = categoryScorer.scoreText(text);
              if (lcsScore != 0) {
                if (lcsScore > maxLcsScore.get()) {
                  maxLcsScore.set(lcsScore);
                  maxLcsScoreText.set(text);
                } else if (lcsScore == maxLcsScore.get()) {
                  maxLcsScoreText.set(maxLcsScoreText.get() + " " + text);
                }
              }
              if (categoryScore != 0) {
                if (categoryScore > maxCategoryScore.get()) {
                  maxCategoryScore.set(categoryScore);
                  maxCategoryScoreText.set(text);
                } else if (categoryScore == maxCategoryScore.get()) {
                  maxCategoryScoreText.set(maxCategoryScoreText.get() + " " + text);
                }
              }
            });
    return maxLcsScoreText.get() != null
        ? maxLcsScoreText.get()
        : (maxCategoryScoreText.get() != null ? maxCategoryScoreText.get() : urlText);
  }

  private Tuple2<List<String>, List<String>> getProductImages(
      List<NodeProperties> imageProperties, Map<String, int[]> imageSrcDimensionMap) {
    final List<String> allImages =
        imageProperties.stream()
            .map(ip -> (List<String>) ip.getProperty(ImageSelector.IMAGE_SRC_URLS))
            .flatMap(Collection::stream)
            .collect(Collectors.toList());

    final int largestImageSize =
        allImages.stream()
            .map(
                i -> {
                  final URI uri;
                  try {
                    uri = new URI(i);
                  } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                  }
                  return uri;
                })
            .filter(
                uri -> imageSrcDimensionMap.containsKey(HtmlUtils.normalizedUriRepresentation(uri)))
            .map(
                uri -> {
                  final int[] dimensions =
                      imageSrcDimensionMap.get(HtmlUtils.normalizedUriRepresentation(uri));
                  return dimensions[0] * dimensions[1];
                })
            .reduce(Integer::max)
            .orElseThrow();

    final List<String> productImages = new LinkedList<>();
    final List<String> otherImages = new LinkedList<>();

    allImages.stream()
        .map(
            i -> {
              final URI uri;
              try {
                uri = new URI(i);
              } catch (URISyntaxException e) {
                throw new RuntimeException(e);
              }
              return uri;
            })
        .forEach(
            uri -> {
              final String mapKey = HtmlUtils.normalizedUriRepresentation(uri);
              if (imageSrcDimensionMap.containsKey(mapKey)) {
                final int[] dimensions = imageSrcDimensionMap.get(mapKey);
                if (dimensions[0] * dimensions[1] == largestImageSize) {
                  productImages.add(uri.toString());
                } else {
                  otherImages.add(uri.toString());
                }
              } else {
                otherImages.add(uri.toString());
              }
            });
    // Just selecting the first among the product Images till we figure out a way to distinguish
    // between the identical images of a srcset
    return new Tuple2<>(Collections.singletonList(productImages.get(0)), otherImages);
  }

  @Override
  public List<ProductSummary> crawlHtml(
      RenderedHtml html, String baseUrl, CrawlContext crawlContext) {
    final PriorityQueue<SubtreeTraversalResult> highestLcsScoreHeap =
        new PriorityQueue<>((r1, r2) -> r2.getChildrenLCSScore() - r1.getChildrenLCSScore());
    final List<SubtreeTraversalResult> productRoots = new LinkedList<>();
    dfs(Jsoup.parse(html.getHtml(), baseUrl), highestLcsScoreHeap);
    SubtreeTraversalResult top = highestLcsScoreHeap.poll();
    while (!highestLcsScoreHeap.isEmpty() && top != null && top.getChildrenLCSScore() > 0) {
      if (top.childrenCountMatchingAllSelectors() > 2) {
        productRoots.add(top);
      }
      top = highestLcsScoreHeap.poll();
    }

    final List<ProductSummary> results = new ArrayList<>();
    productRoots.forEach(
        productRoot ->
            results.addAll(
                productRoot.getChildrenWithAllSelectors().stream()
                    .map(SubtreeTraversalResult::getNodeSelectionResult)
                    .map(
                        esr -> {
                          final NodeProperties urlProperties =
                              esr.getSelectedProperties(ANY_LINK_WITH_HREF_SELECTOR).get(0);
                          final Tuple2<Range<FastMoney>, Range<FastMoney>> priceProperties =
                              getOriginalAndSalePrice(
                                  esr.getSelectedProperties(ANY_PRICE_SELECTOR),
                                  esr.getSelectedProperties(ANY_PRICE_RANGE_SELECTOR));
                          final Tuple2<List<String>, List<String>> imageUrls =
                              getProductImages(
                                  esr.getSelectedProperties(IMAGE_SELECTOR),
                                  html.getImageSrcDimensionMap());
                          final List<NodeProperties> textNodeProperties =
                              esr.getSelectedProperties(TEXT_NODE_SELECTOR);
                          final String productTitle =
                              getProductTitle(textNodeProperties, urlProperties);
                          return new ProductSummary(
                              (String) urlProperties.getProperty(URL_PROPERTY),
                              productTitle,
                              imageUrls._1(),
                              imageUrls._2(),
                              priceProperties._1(),
                              priceProperties._2());
                        })
                    .collect(Collectors.toList())));
    return results;
  }

  public static void main(String[] args) {
    final SummaryCrawler summaryCrawler = new SummaryCrawler(new RenderedHtmlProvider());
    //    List<ProductSummary> productSummaries = summaryCrawler.crawlHtml(
    //        ResourceUtils.readFileContents("dswdummy.html"),
    //        "https://www.chubbiesshorts.com/", new MapCrawlContext(null));
    List<ProductSummary> productSummaries =
        summaryCrawler.crawlUrl(
            "https://www.dsw.com/en/us/brands/steve-madden/N-1z141c7", new MapCrawlContext(null));
    productSummaries.forEach(ps -> log.info("ProductSummary: {}", ps.toString()));
  }
}
