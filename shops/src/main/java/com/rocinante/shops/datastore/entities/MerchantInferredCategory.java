package com.rocinante.shops.datastore.entities;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class MerchantInferredCategory {
  @Id
  private UUID uuid;

  @Column
  private String name;

  @Column
  private String url;

  @Column
  private boolean enabled;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "merchant_uuid")
  private Merchant merchant;

  @OneToMany(fetch =  FetchType.LAZY, mappedBy = "merchantInferredCategory")
  private Set<Product> products = new HashSet<>();

  @Column
  private OffsetDateTime lastCrawledAt;
}
