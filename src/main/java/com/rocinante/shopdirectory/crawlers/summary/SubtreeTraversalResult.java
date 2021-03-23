package com.rocinante.shopdirectory.crawlers.summary;

import com.rocinante.shopdirectory.selectors.ElementSelector;
import com.rocinante.shopdirectory.util.Combinatorics;
import com.rocinante.shopdirectory.lcs.DelimiterLCSToken;
import com.rocinante.shopdirectory.lcs.LCSToken;
import com.rocinante.shopdirectory.lcs.LongestCommonSubsequence;
import com.rocinante.shopdirectory.lcs.StringLCSToken;
import com.rocinante.shopdirectory.selectors.ElementSelectionResult;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.jsoup.nodes.Element;

public class SubtreeTraversalResult {

  private Element rootElement;
  private final List<SubtreeTraversalResult> childResults;
  private final List<SubtreeTraversalResult> childrenWithAllSelectors;
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

  private ElementSelectionResult createElementSelectionResult(ElementSelector... elementSelectors) {
    final ElementSelectionResult elementSelectionResult =
        new ElementSelectionResult(elementSelectors);
    elementSelectionResult.addElementToAllSelectorsMatched(rootElement);

    return this
        .childResults
        .stream()
        .map(SubtreeTraversalResult::getElementSelectionResult)
        .reduce(elementSelectionResult, ElementSelectionResult::merge);
  }

  private List<SubtreeTraversalResult> filterChildrenWithSelectors(ElementSelector... selectors) {
    return getChildResults()
        .stream()
        .filter(child ->
            Arrays
                .stream(selectors)
                .allMatch(selector ->
                    child.getElementSelectionResult().containsSelectorItems(selector)))
        .collect(Collectors.toList());
  }

  public SubtreeTraversalResult(Element root,
      List<SubtreeTraversalResult> subtreeTraversalResults,
      ElementSelector[] elementSelectors) {
    this.rootElement = root;
    this.childResults = subtreeTraversalResults;
    this.serialized = tokenize();
    this.childrenLCSScore =
        computeLCSScore(childResults.stream().map(SubtreeTraversalResult::getSerialized).collect(
            Collectors.toList()));
    this.elementSelectionResult = createElementSelectionResult(elementSelectors);
    this.childrenWithAllSelectors = filterChildrenWithSelectors(elementSelectors);
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

  public List<SubtreeTraversalResult> getChildrenWithAllSelectors() {
    return childrenWithAllSelectors;
  }

  public int childrenCountMatchingAllSelectors() {
    return childrenWithAllSelectors.size();
  }
}
