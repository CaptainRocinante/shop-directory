package com.rocinante.crawlers.summary.selectors;

import static com.rocinante.crawlers.summary.selectors.AnyPriceSelector.PRICE_PATTERN;

import com.google.common.collect.Range;
import com.rocinante.selectors.NodeNotSelected;
import com.rocinante.selectors.NodeProperties;
import com.rocinante.selectors.NodeSelector;
import com.rocinante.util.ResourceUtils;
import io.vavr.control.Either;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import org.javamoney.moneta.FastMoney;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

public class AnyPriceRangeSelector implements NodeSelector {
  public static final String RANGE_MONEY_OBJECT_PROPERTY = "range_money";
  private static final Pattern PRICE_RANGE_PATTERN =
      Pattern.compile(
          String.format(
              "((%s){1})\\s*[-\u2010\u2011\u2012\u2013\u2014\u2015]\\s*((%s){1})",
              PRICE_PATTERN.pattern(), PRICE_PATTERN.pattern()));

  private FastMoney getFastMoney(String rep) {
    final Matcher parsedPrice = PRICE_PATTERN.matcher(rep);

    if (!parsedPrice.matches()) {
      throw new IllegalStateException();
    }
    final String amount =
        parsedPrice.group(2) != null ? parsedPrice.group(2) : parsedPrice.group(3);
    final MonetaryAmount monetaryAmount =
        Monetary.getDefaultAmountFactory()
            .setCurrency("USD")
            .setNumber(Double.parseDouble(amount.replace(",", "")))
            .create();
    return FastMoney.from(monetaryAmount);
  }

  @Override
  public Either<NodeNotSelected, NodeProperties> select(Node node) {
    if (!(node instanceof Element)) {
      return Either.left(NodeNotSelected.getInstance());
    }
    final Element element = (Element) node;
    final Matcher priceRangeMatcher = PRICE_RANGE_PATTERN.matcher(element.text());
    if (priceRangeMatcher.find()) {
      final FastMoney beginningRangePrice = getFastMoney(priceRangeMatcher.group(1));
      final FastMoney endingRangePrice = getFastMoney(priceRangeMatcher.group(7));
      final Map<String, Object> properties = new HashMap<>();
      properties.put(
          RANGE_MONEY_OBJECT_PROPERTY, Range.closed(beginningRangePrice, endingRangePrice));
      return Either.right(new NodeProperties(node, properties));
    } else {
      return Either.left(NodeNotSelected.getInstance());
    }
  }

  public static void main(String[] args) {
    AnyPriceRangeSelector selector = new AnyPriceRangeSelector();
    Document document = Jsoup.parse(ResourceUtils.readFileContents("dswdummy.html"));
    var result =
        selector.select((((document.childNodes().get(0)).childNodes()).get(3)).childNodes().get(1));
  }
}
