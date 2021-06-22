package com.rocinante.shops.search;

import com.rocinante.common.api.dto.BnplFilterDto;
import com.rocinante.common.api.dto.MerchantFilterDto;
import com.rocinante.datastore.entities.Product;
import java.util.List;
import lombok.Data;

@Data
public class SearchServiceResults {
  private final long totalResultsCount;
  private final List<Product> currentPageResults;
  private final List<BnplFilterDto> bnplFiltersSelectedDtos;
  private final List<BnplFilterDto> bnplFilterDtos;
  private final List<MerchantFilterDto> merchantSelectedFilterDtos;
  private final List<MerchantFilterDto> merchantFilterDtos;
}
