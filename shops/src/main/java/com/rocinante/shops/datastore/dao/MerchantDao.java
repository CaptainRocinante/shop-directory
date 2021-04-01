package com.rocinante.shops.datastore.dao;

import com.rocinante.shops.datastore.entities.Merchant;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MerchantDao extends JpaRepository<Merchant, UUID> {
  Optional<Merchant> findByUuidAndLastCrawledAtBefore(UUID uuid, OffsetDateTime lastCrawledAt);
}
