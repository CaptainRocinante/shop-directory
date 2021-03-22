package com.rocinante.shopdirectory.selectors;

public class ElementNotSelected {
  private static ElementNotSelected instance;

  private ElementNotSelected() {
  }

  public static ElementNotSelected getInstance() {
    if (instance == null) {
      instance = new ElementNotSelected();
    }
    return instance;
  }
}
