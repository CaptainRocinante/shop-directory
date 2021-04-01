package com.rocinante.shops.utils;

import javax.annotation.Nullable;

public class NullabilityUtils {
  public static <T extends Comparable<T>> boolean areObjectsEqual(@Nullable T obj1,
      @Nullable T obj2) {
    if (obj1 == null && obj2 == null) {
      return true;
    } else if (obj1 != null && obj2 != null) {
      return obj1.compareTo(obj2) == 0;
    } else {
      return false;
    }
  }
}
