package com.rocinante.shops.datastore.entities;

import com.neovisionaries.i18n.CountryCode;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Setter
@Getter
public class Merchant {
  @Id
  private UUID uuid;

  @Column
  private String name;

  @Column
  @Type(type = "com.rocinante.shops.datastore.types.UrlType")
  private URL url;

  @Column
  @Type(type = "com.rocinante.shops.datastore.types.CountryCodeType")
  private CountryCode countryCode;

  private boolean enabled;

  @ManyToMany(mappedBy = "merchants", fetch = FetchType.LAZY)
  private Set<BnplProvider> bnplProviders;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch =  FetchType.LAZY, mappedBy =
      "merchant")
  private Set<MerchantInferredCategory> merchantInferredCategories = new HashSet<>();

  @Column
  private OffsetDateTime createdAt;

  @Column
  private OffsetDateTime updatedAt;

  @Column
  private OffsetDateTime lastCrawledAt;
}
