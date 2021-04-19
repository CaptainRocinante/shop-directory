package com.rocinante.datastore.dao;

import com.rocinante.datastore.entities.Merchant;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MerchantDao extends JpaRepository<Merchant, UUID> {
  @Query(
      value =
          "SELECT * FROM merchant WHERE uuid = ?1 AND (last_crawled_at < ?2 OR "
              + "last_crawled_at IS NULL)",
      nativeQuery = true)
  Optional<Merchant> findByUuidAndLastCrawledAtBeforeOrNull(
      UUID uuid, OffsetDateTime lastCrawledAt);

  Optional<Merchant> findByUrl(URL url);
}
