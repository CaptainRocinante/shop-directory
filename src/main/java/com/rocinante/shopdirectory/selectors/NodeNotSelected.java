package com.rocinante.shopdirectory.selectors;

import java.util.Objects;

public class NodeNotSelected {
  private static NodeNotSelected instance;

  private NodeNotSelected() {
  }

  public static NodeNotSelected getInstance() {
    if (instance == null) {
      instance = new NodeNotSelected();
    }
    return instance;
  }

  @Override
  public int hashCode() {
    return Objects.hash(NodeNotSelected.class.getName());
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof NodeNotSelected;
  }
}
