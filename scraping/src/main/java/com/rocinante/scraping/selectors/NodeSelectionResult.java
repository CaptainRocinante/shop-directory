package com.rocinante.scraping.selectors;

import io.vavr.control.Either;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.jsoup.nodes.Node;

public class NodeSelectionResult {
  private final Map<NodeSelector, List<NodeProperties>> nodeSelectorMap;

  public NodeSelectionResult() {
    this.nodeSelectorMap = new HashMap<>();
  }

  public NodeSelectionResult(NodeSelector... selectorsToTest) {
    this.nodeSelectorMap = new HashMap<>();
    Arrays.stream(selectorsToTest).forEach(sl -> nodeSelectorMap.put(sl, new LinkedList<>()));
  }

  public NodeSelectionResult merge(NodeSelectionResult other) {
    final NodeSelectionResult nodeSelectionResult = new NodeSelectionResult();
    nodeSelectionResult.nodeSelectorMap.putAll(this.nodeSelectorMap);

    for (final Map.Entry<NodeSelector, List<NodeProperties>> e : other.nodeSelectorMap.entrySet()) {
      if (nodeSelectionResult.nodeSelectorMap.containsKey(e.getKey())) {
        nodeSelectionResult.nodeSelectorMap.get(e.getKey()).addAll(e.getValue());
      } else {
        nodeSelectionResult.nodeSelectorMap.put(e.getKey(), e.getValue());
      }
    }
    return nodeSelectionResult;
  }

  public void addNodeToAllSelectorsMatched(Node node) {
    nodeSelectorMap
        .keySet()
        .forEach(
            nodeSelector -> {
              final Either<NodeNotSelected, NodeProperties> selectionResult =
                  nodeSelector.select(node);
              if (selectionResult.isRight()) {
                nodeSelectorMap.get(nodeSelector).add(selectionResult.get());
              }
            });
  }

  public boolean containsSelectorItems(NodeSelector nodeSelector) {
    if (!nodeSelectorMap.containsKey(nodeSelector)) {
      return false;
    } else {
      return !nodeSelectorMap.get(nodeSelector).isEmpty();
    }
  }

  public List<NodeProperties> getSelectedProperties(NodeSelector nodeSelector) {
    return nodeSelectorMap.get(nodeSelector);
  }
}
