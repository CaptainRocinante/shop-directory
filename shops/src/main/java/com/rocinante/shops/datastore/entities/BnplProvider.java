package com.rocinante.shops.datastore.entities;

import java.net.URL;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

@NoArgsConstructor
@AllArgsConstructor
@Entity
public class BnplProvider {
  @Id
  private UUID uuid;

  @Column
  private String name;

  @Column
  @Type(type = "com.rocinante.shops.datastore.types.UrlType")
  private URL url;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "bnpl_provider_merchant_mapping",
      joinColumns = { @JoinColumn(name = "bnpl_provider_uuid") },
      inverseJoinColumns = { @JoinColumn(name = "merchant_uuid") }
  )
  private Set<Merchant> merchants = new HashSet<>();

  @Column
  private OffsetDateTime createdAt;

  @Column
  private OffsetDateTime updatedAt;
}
