package com.rocinante.shops.datastore.entities;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Entity
public class BnplProvider {
  @Id
  private UUID uuid;

  @Column
  private String name;

  @Column
  private String url;

  @OneToMany(fetch =  FetchType.LAZY, mappedBy = "bnplProvider")
  private Set<Merchant> merchants = new HashSet<>();

  @Column
  private OffsetDateTime createdAt;

  @Column
  private OffsetDateTime updatedAt;
}
