package com.rocinante.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public enum Mode {
  CATEGORIES("categories"),
  PRODUCTS("products");

  private final String key;

  // Reverse-lookup map for getting a day from an abbreviation
  private static final Map<String, Mode> LOOKUP = new HashMap<>();

  static {
    for (Mode m : Mode.values()) {
      LOOKUP.put(m.getKey(), m);
    }
  }

  Mode(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }

  public static Mode fromKey(String key) {
    return Objects.requireNonNull(LOOKUP.get(key));
  }
}
