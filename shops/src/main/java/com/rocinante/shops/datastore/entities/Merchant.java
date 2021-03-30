package com.rocinante.shops.datastore.entities;

import com.neovisionaries.i18n.CountryCode;
import com.rocinante.shops.datastore.entities.BnplProvider;
import java.time.OffsetDateTime;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import org.hibernate.annotations.Type;

@Entity
public class Merchant {
  @Id
  private UUID uuid;

  @Column
  private String name;

  @Column
  private String url;

  @Column(nullable = false)
  @Type(type = "com.rocinante.shops.datastore.types.CountryCodeType")
  private CountryCode countryCode;

  @Column(nullable = false)
  private boolean enabled;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "bnpl_provider_uuid")
  private BnplProvider bnplProvider;

  @Column
  private OffsetDateTime createdAt;

  @Column
  private OffsetDateTime updatedAt;

  @Column
  private OffsetDateTime lastCrawledAt;
}
