package com.rocinante.crawlers.category;

import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@ToString
@Getter
public class Category {
  private final String categoryUrl;
  private final String categoryName;

  @Override
  public int hashCode() {
    return Objects.hash(categoryUrl);
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof Category
        && (((Category) obj).categoryUrl.equalsIgnoreCase(this.categoryUrl));
  }
}
