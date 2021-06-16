package com.rocinante.shops.search;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class SearchServiceQuery {
  private final String query;
  private final List<SingleFieldSearchQueryParam> singleFieldSearchQueryParamList;

  private SearchServiceQuery(Builder builder) {
    this.query = builder.query;
    this.singleFieldSearchQueryParamList = builder.singleFieldSearchQueryParamList;
  }

  public static class Builder {
    private final String query;
    private final List<SingleFieldSearchQueryParam> singleFieldSearchQueryParamList;

    public Builder(String query) {
      this.query = query;
      this.singleFieldSearchQueryParamList = new ArrayList<>();
    }

    public Builder addQueryParam(SingleFieldSearchQueryParam singleFieldSearchQueryParam) {
      this.singleFieldSearchQueryParamList.add(singleFieldSearchQueryParam);
      return this;
    }

    public SearchServiceQuery build() {
      return new SearchServiceQuery(this);
    }
  }
}
