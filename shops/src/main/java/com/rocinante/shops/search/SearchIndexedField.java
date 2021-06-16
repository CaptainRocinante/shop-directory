package com.rocinante.shops.search;

public enum SearchIndexedField {
  NAME("name"),
  MERCHANT_CATEGORY("merchantInferredCategoryText"),
  MERCHANT_NAME("merchantNameText"),
  BNPL_NAME("bnplNameText");

  private final String fieldName;

  SearchIndexedField(String fieldName) {
    this.fieldName = fieldName;
  }

  public String getFieldName() {
    return fieldName;
  }
}
