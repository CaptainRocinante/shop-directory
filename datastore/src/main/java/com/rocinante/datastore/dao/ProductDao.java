package com.rocinante.datastore.dao;

import com.rocinante.datastore.entities.Product;
import java.net.URL;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductDao extends JpaRepository<Product, UUID> {
  Optional<Product> findByUrl(URL url);
}
