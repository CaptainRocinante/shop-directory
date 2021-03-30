package com.rocinante.shops.datastore;

import com.neovisionaries.i18n.CountryCode;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import org.hibernate.annotations.Type;

@Entity
public class BnplProvider {
  @Id
  private UUID uuid;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String url;

  @Column(nullable = false)
  @Type(type = "com.rocinante.shops.datastore.types.CountryCodeType")
  private CountryCode countryCode;

  @OneToMany(fetch =  FetchType.LAZY, mappedBy = "bnplProvider")
  private Set<Merchant> merchants = new HashSet<>();

  @Column(nullable = false)
  private OffsetDateTime createdAt;

  @Column
  private OffsetDateTime updatedAt;
}
