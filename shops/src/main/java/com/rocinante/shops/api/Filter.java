package com.rocinante.shops.api;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Filter {
  public static final String ON = "on";
  public static final String OFF = "off";

  private final String filterId;
  private final String onOff;
}
