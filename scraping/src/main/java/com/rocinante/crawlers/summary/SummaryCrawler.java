package com.rocinante.crawlers.summary;

import static com.rocinante.crawlers.summary.selectors.AnyLinkWithHrefTextSelector.TEXT_PROPERTY;
import static com.rocinante.crawlers.summary.selectors.AnyLinkWithHrefTextSelector.URL_PROPERTY;
import static com.rocinante.crawlers.summary.selectors.AnyPriceRangeSelector.RANGE_MONEY_OBJECT_PROPERTY;
import static com.rocinante.crawlers.summary.selectors.AnyPriceSelector.LIST_MONEY_OBJECT_PROPERTY;

import com.google.common.collect.Range;
import com.rocinante.crawlers.CrawlContext;
import com.rocinante.crawlers.Crawler;
import com.rocinante.crawlers.CrawlerType;
import com.rocinante.crawlers.MapCrawlContext;
import com.rocinante.crawlers.category.CategoryScorer;
import com.rocinante.crawlers.summary.selectors.AnyLinkWithHrefTextSelector;
import com.rocinante.crawlers.summary.selectors.AnyPriceRangeSelector;
import com.rocinante.crawlers.summary.selectors.AnyPriceSelector;
import com.rocinante.crawlers.summary.selectors.ImageSelector;
import com.rocinante.crawlers.summary.selectors.TextNodeSelector;
import com.rocinante.html.RenderedHtml;
import com.rocinante.html.RenderedHtmlProvider;
import com.rocinante.lcs.LongestCommonSubsequence;
import com.rocinante.lcs.StringLCSToken;
import com.rocinante.selectors.NodeProperties;
import com.rocinante.selectors.NodeSelector;
import com.rocinante.util.MoneyUtils;
import com.rocinante.util.Tokenizer;
import com.rocinante.util.UrlUtils;
import io.vavr.Tuple2;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
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
          highestAmount.isGreaterThan(lowestAmount) ? Range.singleton(highestAmount) : null,
          Range.singleton(lowestAmount));
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
        return new Tuple2<>(null, priceRange);
      }
    }
  }

  private String getProductTitle(
      List<NodeProperties> allTextNodesSelected, String url, String urlText) {
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
                  final List<StringLCSToken> existingMaxTokens =
                      Tokenizer.alphaNumericLcsTokens(maxLcsScoreText.get());
                  final int lcsScoreAmongDescriptionCandidates =
                      LongestCommonSubsequence.computeLcsStringTokensOnly(
                          textTokens, existingMaxTokens);
                  if (lcsScoreAmongDescriptionCandidates <= 2) {
                    maxLcsScoreText.set(maxLcsScoreText.get() + " " + text);
                  }
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

  @Nullable
  private int[] getUrlOrUriDimensions(String input, Map<String, int[]> imageSrcDimensionMap) {
    try {
      final URL url = new URL(input);
      if (imageSrcDimensionMap.containsKey(url.toString())) {
        return imageSrcDimensionMap.get(url.toString());
      } else {
        final URI uri = url.toURI();
        final String key = UrlUtils.domainRemovedUriRepresentation(uri);
        if (imageSrcDimensionMap.containsKey(key)) {
          return imageSrcDimensionMap.get(key);
        } else {
          final Set<String> allKeys = imageSrcDimensionMap.keySet();
          final Optional<String> keyContainedInUrl =
              allKeys.stream().filter(input::contains).findFirst();
          if (keyContainedInUrl.isPresent()) {
            return imageSrcDimensionMap.get(keyContainedInUrl.get());
          }
        }
      }
    } catch (MalformedURLException | URISyntaxException e) {
      throw new RuntimeException(e);
    }
    return null;
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
                url -> {
                  final int[] dimensions = getUrlOrUriDimensions(url, imageSrcDimensionMap);
                  if (dimensions == null) {
                    return Integer.MIN_VALUE;
                  } else {
                    return dimensions[0] * dimensions[1];
                  }
                })
            .reduce(Integer::max)
            .orElseThrow();

    final List<String> productImages = new LinkedList<>();
    final List<String> otherImages = new LinkedList<>();

    allImages.forEach(
        url -> {
          final int[] dimensions = getUrlOrUriDimensions(url, imageSrcDimensionMap);
          if (dimensions != null) {
            if (dimensions[0] * dimensions[1] == largestImageSize) {
              productImages.add(url);
            } else {
              otherImages.add(url);
            }
          }
        });

    // Just selecting the first among the product Images till we figure out a way to distinguish
    // between the identical images of a srcset
    return new Tuple2<>(Collections.singletonList(productImages.get(0)), otherImages);
  }

  // Select the longest link found in the product grid
  private Tuple2<String, String> identifyProductLinkWithText(
      List<NodeProperties> nodePropertiesList) {
    return nodePropertiesList.stream()
        .map(
            np ->
                new Tuple2<>(
                    (String) np.getProperty(URL_PROPERTY), (String) np.getProperty(TEXT_PROPERTY)))
        .reduce((t1, t2) -> t1._1().trim().length() > t2._1().trim().length() ? t1 : t2)
        .orElseThrow();
  }

  @Override
  public List<ProductSummary> crawlHtml(
      RenderedHtml html, String baseUrl, CrawlContext crawlContext) {
    //    log.info(html.getHtml());
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
        productRoot -> {
          var innerResults =
              productRoot.getChildrenWithAllSelectors().stream()
                  .map(SubtreeTraversalResult::getNodeSelectionResult)
                  .map(
                      esr -> {
                        final Tuple2<String, String> urlAndText =
                            identifyProductLinkWithText(
                                esr.getSelectedProperties(ANY_LINK_WITH_HREF_SELECTOR));
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
                            getProductTitle(textNodeProperties, urlAndText._1(), urlAndText._2());
                        return new ProductSummary(
                            urlAndText._1(),
                            productTitle,
                            imageUrls._1(),
                            imageUrls._2(),
                            priceProperties._1(),
                            priceProperties._2());
                      })
                  .collect(Collectors.toList());
          results.addAll(innerResults);
        });
    return results;
  }

  public static void main(String[] args) {
    final SummaryCrawler summaryCrawler = new SummaryCrawler(new RenderedHtmlProvider());
    //    List<ProductSummary> productSummaries = summaryCrawler.crawlHtml(
    //        ResourceUtils.readFileContents("dswdummy.html"),
    //        "https://www.chubbiesshorts.com/", new MapCrawlContext(null));
    List<ProductSummary> productSummaries =
        summaryCrawler.crawlUrl(
            //
            // "https://www.aritzia.com/en/clothing/womens-workout-clothes/womens-bike-shorts",
            "https://www.freepeople.com/tops/", new MapCrawlContext(null));
    productSummaries.forEach(ps -> log.info("ProductSummary: {}", ps.toString()));
  }
}
