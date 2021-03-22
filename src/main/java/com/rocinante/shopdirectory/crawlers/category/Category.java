package com.rocinante.shopdirectory.crawlers.category;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Category {
  private final String categoryUrl;
  private final String categoryName;
}
