package com.rocinante.shops.api;

import com.rocinante.common.api.dto.ProductDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SearchResponse {
  private final List<ProductDto> products;
}
