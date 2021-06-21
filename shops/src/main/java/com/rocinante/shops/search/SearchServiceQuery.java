package com.rocinante.shops.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import lombok.Data;

@Data
public class SearchServiceQuery {
  private final String query;
  private final List<SingleFieldSearchQueryParam> singleFieldSearchQueryParamList;
  private final List<UUID> userAppliedBnplFilters;
  private final List<UUID> userAppliedMerchantFilters;

  private SearchServiceQuery(Builder builder) {
    this.query = builder.query;
    this.singleFieldSearchQueryParamList = builder.singleFieldSearchQueryParamList;
    this.userAppliedBnplFilters = builder.userAppliedBnplFilters;
    this.userAppliedMerchantFilters = builder.userAppliedMerchantFilters;
  }

  public static class Builder {
    private final String query;
    private final List<SingleFieldSearchQueryParam> singleFieldSearchQueryParamList;
    private final List<UUID> userAppliedBnplFilters;
    private final List<UUID> userAppliedMerchantFilters;

    public Builder(String query) {
      this.query = query;
      this.singleFieldSearchQueryParamList = new ArrayList<>();
      this.userAppliedBnplFilters = new ArrayList<>();
      this.userAppliedMerchantFilters = new ArrayList<>();
    }

    public Builder addQueryParam(SingleFieldSearchQueryParam singleFieldSearchQueryParam) {
      this.singleFieldSearchQueryParamList.add(singleFieldSearchQueryParam);
      return this;
    }

    public Builder addUserAppliedBnplFilters(@Nullable List<String> userAppliedBnplFilters) {
      this.userAppliedBnplFilters.addAll(userAppliedBnplFilters == null
          ? Collections.emptyList()
          : userAppliedBnplFilters.stream().map(UUID::fromString).collect(Collectors.toList()));
      return this;
    }

    public Builder addUserAppliedMerchantFilters(@Nullable List<String> userAppliedMerchantFilters) {
      this.userAppliedMerchantFilters.addAll(userAppliedMerchantFilters == null
          ? Collections.emptyList()
          : userAppliedMerchantFilters.stream().map(UUID::fromString).collect(Collectors.toList()));
      return this;
    }

    public SearchServiceQuery build() {
      return new SearchServiceQuery(this);
    }
  }
}
