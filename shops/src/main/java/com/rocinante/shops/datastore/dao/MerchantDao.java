package com.rocinante.shops.datastore.dao;

import com.rocinante.shops.datastore.entities.Merchant;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MerchantDao extends JpaRepository<Merchant, UUID> {
  @Query(
      value =
          "SELECT * FROM merchant WHERE uuid = ?1 AND (last_crawled_at < ?2 OR "
              + "last_crawled_at IS NULL)",
      nativeQuery = true)
  Optional<Merchant> findByUuidAndLastCrawledAtBeforeOrNull(
      UUID uuid, OffsetDateTime lastCrawledAt);
}
