package com.rocinante.shops.datastore.dao;

import com.rocinante.shops.datastore.entities.Product;
import java.net.URL;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductDao extends JpaRepository<Product, UUID> {
  Optional<Product> findByUrl(URL url);
}
