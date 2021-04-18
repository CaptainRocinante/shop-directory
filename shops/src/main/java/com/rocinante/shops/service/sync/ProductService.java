package com.rocinante.shops.service.sync;

import com.rocinante.common.api.dto.ProductCrudDto;
import com.rocinante.datastore.dao.ProductDao;
import com.rocinante.datastore.entities.Product;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class ProductService {
  private final ProductDao productDao;

  @Transactional
  public void updateProduct(ProductCrudDto productCrudDto) {
    final Product product = productDao.getOne(UUID.fromString(productCrudDto.getUuid()));
    if (product.applyUpdatesIfNeeded(productCrudDto)) {
      productDao.save(product);
    }
  }
}
