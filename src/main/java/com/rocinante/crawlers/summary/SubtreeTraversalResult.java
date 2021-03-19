package com.rocinante.crawlers.summary;

import com.rocinante.crawlers.infrastructure.Combinatorics;
import com.rocinante.crawlers.infrastructure.lcs.DelimiterLCSToken;
import com.rocinante.crawlers.infrastructure.lcs.LCSToken;
import com.rocinante.crawlers.infrastructure.lcs.LongestCommonSubsequence;
import com.rocinante.crawlers.infrastructure.lcs.StringLCSToken;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

public class SubtreeTraversalResult {
  private final StringLCSToken rootTag;
  private final String rootText;
  private final List<SubtreeTraversalResult> childResults;
  private final List<LCSToken> serialized;
  private final int childrenLCSScore;

  private List<LCSToken> tokenize() {
    final List<LCSToken> tokens = new LinkedList<>();
    tokens.add(rootTag);
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

  public SubtreeTraversalResult(Element root,
      List<SubtreeTraversalResult> subtreeTraversalResults) {
    this.rootTag = new StringLCSToken(root.tagName());
    this.rootText = root.text();
    this.childResults = subtreeTraversalResults;
    this.serialized = tokenize();
    this.childrenLCSScore =
        computeLCSScore(childResults.stream().map(SubtreeTraversalResult::getSerialized).collect(
            Collectors.toList()));
  }

  public List<LCSToken> getSerialized() {
    return serialized;
  }

  public int getChildrenLCSScore() {
    return childrenLCSScore;
  }
}
