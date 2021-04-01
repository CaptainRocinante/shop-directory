package com.rocinante.shops.datastore.dao;

import com.rocinante.shops.datastore.entities.MerchantInferredCategory;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MerchantInferredCategoryDao extends JpaRepository<MerchantInferredCategory,
    UUID> {
  Optional<MerchantInferredCategory> findByMerchantUuidAndUrl(UUID merchantUuid,
      URL url);

  List<MerchantInferredCategory> findByMerchantUuidAndLastCrawledAtBefore(UUID merchantUuid,
      OffsetDateTime lastCrawledAt);
}
