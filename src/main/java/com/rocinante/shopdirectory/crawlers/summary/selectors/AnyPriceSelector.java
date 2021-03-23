package com.rocinante.shopdirectory.crawlers.summary.selectors;

import com.rocinante.shopdirectory.selectors.ElementNotSelected;
import com.rocinante.shopdirectory.selectors.ElementProperties;
import com.rocinante.shopdirectory.selectors.ElementSelector;
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

public class AnyPriceSelector implements ElementSelector {
  public static final String LIST_MONEY_OBJECT_PROPERTY = "list_money";
  private static final Pattern PRICE_PATTERN = Pattern.compile("(USD|\\$)\\s*(\\d{1,3}(?:["
      + ".,]\\d{3})*(?:[.,]\\d{2})?)|(\\d{1,3}(?:[.,]\\d{3})*(?:[.,]\\d{2})?)\\s*?(USD|\\$)");

  @Override
  public Either<ElementNotSelected, ElementProperties> select(Element element) {
    final Matcher matcher = PRICE_PATTERN.matcher(element.text());
    final List<MatchResult> matchResults = matcher.results().collect(Collectors.toList());
    if (matchResults.size() == 0) {
      return Either.left(ElementNotSelected.getInstance());
    } else {
      final Map<String, Object> properties = new HashMap<>();
      final List<FastMoney> allMoney = new ArrayList<>();

      matchResults
          .forEach(result -> {
            final String amount = result.group(2) != null ? result.group(2) : matcher.group(3);
            final MonetaryAmount monetaryAmount = Monetary
                .getDefaultAmountFactory()
                .setCurrency("USD")
                .setNumber(Double.parseDouble(amount.replace(",", "")))
                .create();
            allMoney.add(FastMoney.from(monetaryAmount));
          });
      properties.put(LIST_MONEY_OBJECT_PROPERTY, allMoney);
      return Either.right(new ElementProperties(element, properties));
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

  public static void main(String[] args) {
    AnyPriceSelector selector = new AnyPriceSelector();
  }
}
