package com.rocinante.shops.search;

import com.rocinante.datastore.entities.Product;
import java.util.List;
import lombok.Data;

@Data
public class SearchServiceResults {
  private final long totalResultsCount;
  private final List<Product> currentPageResults;
}
