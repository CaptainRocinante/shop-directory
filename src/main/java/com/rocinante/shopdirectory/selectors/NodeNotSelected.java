package com.rocinante.shopdirectory.selectors;

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
}
