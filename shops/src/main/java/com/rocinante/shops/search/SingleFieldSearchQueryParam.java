package com.rocinante.shops.search;

import lombok.Data;

@Data
public class SingleFieldSearchQueryParam {
  private final SearchIndexedField searchIndexedField;
  private final float weight;
  private final boolean required;
}
