package com.rocinante.scraping.crawlers.summary.selectors;

import com.rocinante.scraping.selectors.NodeNotSelected;
import com.rocinante.scraping.selectors.NodeProperties;
import com.rocinante.scraping.selectors.NodeSelector;
import io.vavr.control.Either;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import org.javamoney.moneta.FastMoney;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

public class AnyPriceSelector implements NodeSelector {
  public static final String LIST_MONEY_OBJECT_PROPERTY = "list_money";
  private static final Pattern CURRENCY_FIRST_PATTERN =
      Pattern.compile("\\s*(USD|\\$)\\s*(\\d{1,3}(?:[.,]\\d{3})*(?:[.,]\\d{2})?)\\s*");
  private static final Pattern AMOUNT_FIRST_PATTERN =
      Pattern.compile("\\s*(\\d{1,3}(?:[.,]\\d{3})*(?:[.,]\\d{2})?)\\s*(USD|\\$)\\s*");

  @Override
  public Either<NodeNotSelected, NodeProperties> select(Node node) {
    if (!(node instanceof Element)) {
      return Either.left(NodeNotSelected.getInstance());
    }
    final Element element = (Element) node;
    final Matcher currencyFirstMatcher = CURRENCY_FIRST_PATTERN.matcher(element.text());
    final Matcher amountFirstMatcher = AMOUNT_FIRST_PATTERN.matcher(element.text());
    final List<MatchResult> currencyFirstMatchResults =
        currencyFirstMatcher.results().collect(Collectors.toList());
    final List<MatchResult> amountFirstMatchResults =
        amountFirstMatcher.results().collect(Collectors.toList());
    if (currencyFirstMatchResults.isEmpty() && amountFirstMatchResults.isEmpty()) {
      return Either.left(NodeNotSelected.getInstance());
    } else {
      final List<MatchResult> matchResults =
          currencyFirstMatchResults.isEmpty() ? amountFirstMatchResults : currencyFirstMatchResults;
      final Map<String, Object> properties = new HashMap<>();
      final List<FastMoney> allMoney = new ArrayList<>();

      matchResults.forEach(
          result -> {
            final String amount = result.group(2) != null ? result.group(2) : result.group(3);
            final MonetaryAmount monetaryAmount;
            try {
               monetaryAmount = Monetary.getDefaultAmountFactory()
                      .setCurrency("USD") // TODO: Hardcoded for now
                      .setNumber(Double.parseDouble(amount.replace(",", "")))
                      .create();
            } catch (NumberFormatException e) {
              return;
            }
            allMoney.add(FastMoney.from(monetaryAmount));
          });
      if (allMoney.isEmpty()) {
        return Either.left(NodeNotSelected.getInstance());
      }
      properties.put(LIST_MONEY_OBJECT_PROPERTY, allMoney);
      return Either.right(new NodeProperties(element, properties));
    }
  }

  @Override
  public int hashCode() {
    return Objects.hash(AnyPriceSelector.class.getName());
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof AnyPriceSelector;
  }

  public static void main(String[] args) {}
}
