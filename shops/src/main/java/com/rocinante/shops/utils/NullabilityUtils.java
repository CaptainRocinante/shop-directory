package com.rocinante.shops.utils;

import javax.annotation.Nullable;

public class NullabilityUtils {
  public static <T> boolean equals(@Nullable T obj1, @Nullable T obj2) {
    if (obj1 == null && obj2 == null) {
      return true;
    } else if (obj1 != null && obj2 != null) {
      return obj1.equals(obj2);
    } else {
      return false;
    }
  }
}
