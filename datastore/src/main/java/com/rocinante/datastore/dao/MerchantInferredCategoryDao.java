package com.rocinante.datastore.dao;

import com.rocinante.datastore.entities.MerchantInferredCategory;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MerchantInferredCategoryDao extends JpaRepository<MerchantInferredCategory, UUID> {
  Optional<MerchantInferredCategory> findByMerchantUuidAndUrl(UUID merchantUuid, URL url);

  @Query(
      value =
          "SELECT * FROM merchant_inferred_category WHERE merchant_uuid = ?1 AND "
              + "(last_crawled_at < ?2 OR last_crawled_at IS NULL)",
      nativeQuery = true)
  List<MerchantInferredCategory> findByMerchantUuidAndLastCrawledAtBeforeOrNull(
      UUID merchantUuid, OffsetDateTime lastCrawledAt);
}
