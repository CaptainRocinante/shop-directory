package com.rocinante.crawlers.summary;

import com.rocinante.crawlers.infrastructure.Combinatorics;
import com.rocinante.crawlers.infrastructure.lcs.DelimiterLCSToken;
import com.rocinante.crawlers.infrastructure.lcs.LCSToken;
import com.rocinante.crawlers.infrastructure.lcs.LongestCommonSubsequence;
import com.rocinante.crawlers.infrastructure.lcs.StringLCSToken;
import com.rocinante.crawlers.infrastructure.selectors.ElementSelectionResult;
import com.rocinante.crawlers.summary.selectors.AnyLinkWithHrefTextSelector;
import com.rocinante.crawlers.summary.selectors.AnyPriceSelector;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.jsoup.nodes.Element;

public class SubtreeTraversalResult {
  public static final AnyLinkWithHrefTextSelector ANY_LINK_WITH_HREF_SELECTOR =
      new AnyLinkWithHrefTextSelector();
  public static final AnyPriceSelector ANY_PRICE_SELECTOR = new AnyPriceSelector();

  private Element rootElement;
  private final List<SubtreeTraversalResult> childResults;
  private final List<LCSToken> serialized;
  private final int childrenLCSScore;
  private final ElementSelectionResult elementSelectionResult;

  private List<LCSToken> tokenize() {
    final List<LCSToken> tokens = new LinkedList<>();
    tokens.add(new StringLCSToken(rootElement.tagName()));
    if (!childResults.isEmpty()) {
      tokens.add(new DelimiterLCSToken());
      tokens.addAll(
          childResults
              .stream()
              .map(SubtreeTraversalResult::getSerialized)
              .flatMap(Collection::stream)
              .collect(Collectors.toList())
      );
      tokens.add(new DelimiterLCSToken());
    }
    return tokens;
  }

  private int computeLCSScore(List<List<LCSToken>> childTokens) {
    if (childTokens.size() < 4) {
      return 0;
    }

    int lcsScore = 0;
    final List<int[]> tupleIndices = Combinatorics.generateNcR(childTokens.size(), 2);

    for (int[] tupleIndex : tupleIndices) {
      final List<LCSToken> first = childTokens.get(tupleIndex[0]);
      final List<LCSToken> second = childTokens.get(tupleIndex[1]);

      final int lcs = LongestCommonSubsequence.computeLcs(first, second);
      lcsScore = lcsScore + ((lcs < 3) ? 0 : lcs);
    }
    return lcsScore;
  }

  private ElementSelectionResult createElementSelectionResult() {
    final ElementSelectionResult elementSelectionResult =
        new ElementSelectionResult(ANY_LINK_WITH_HREF_SELECTOR, ANY_PRICE_SELECTOR);
    elementSelectionResult.addElementToAllSelectorsMatched(rootElement);

    return this
        .childResults
        .stream()
        .map(SubtreeTraversalResult::getElementSelectionResult)
        .reduce(elementSelectionResult, ElementSelectionResult::merge);
  }

  public SubtreeTraversalResult(Element root,
      List<SubtreeTraversalResult> subtreeTraversalResults) {
    this.rootElement = root;
    this.childResults = subtreeTraversalResults;
    this.serialized = tokenize();
    this.childrenLCSScore =
        computeLCSScore(childResults.stream().map(SubtreeTraversalResult::getSerialized).collect(
            Collectors.toList()));
    this.elementSelectionResult = createElementSelectionResult();
  }

  public List<LCSToken> getSerialized() {
    return serialized;
  }

  public int getChildrenLCSScore() {
    return childrenLCSScore;
  }

  public List<SubtreeTraversalResult> getChildResults() {
    return childResults;
  }

  public ElementSelectionResult getElementSelectionResult() {
    return elementSelectionResult;
  }
}
