package com.rocinante.shops.datastore.dao;

import com.rocinante.shops.datastore.entities.MerchantInferredCategory;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MerchantInferredCategoryDao extends JpaRepository<MerchantInferredCategory,
    UUID> {
  Optional<MerchantInferredCategory> findByMerchantUuidAndUrl(UUID merchantUuid,
      String url);
}
