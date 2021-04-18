package com.rocinante.scraping.util;

import org.javamoney.moneta.FastMoney;

public class MoneyUtils {
  public static FastMoney max(FastMoney money1, FastMoney money2) {
    if (money1.isGreaterThanOrEqualTo(money2)) {
      return money1;
    } else {
      return money2;
    }
  }

  public static FastMoney min(FastMoney money1, FastMoney money2) {
    if (money1.isLessThanOrEqualTo(money2)) {
      return money1;
    } else {
      return money2;
    }
  }
}
