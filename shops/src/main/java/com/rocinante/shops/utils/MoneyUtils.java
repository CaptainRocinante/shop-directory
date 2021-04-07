package com.rocinante.shops.utils;

import com.neovisionaries.i18n.CurrencyCode;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class MoneyUtils {
  public static String getFormattedAmount(CurrencyCode currencyCode, BigDecimal bigDecimal) {
    return bigDecimal.setScale(currencyCode.getCurrency().getDefaultFractionDigits(),
        RoundingMode.DOWN).toString();
  }
}
