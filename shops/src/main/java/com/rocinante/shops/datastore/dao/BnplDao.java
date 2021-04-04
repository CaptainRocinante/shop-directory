package com.rocinante.shops.datastore.dao;

import com.rocinante.shops.datastore.entities.BnplProvider;
import java.net.URL;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BnplDao extends JpaRepository<BnplProvider, UUID> {
  Optional<BnplProvider> findByUrl(URL url);
}
