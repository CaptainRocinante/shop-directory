package com.rocinante.shops.datastore.entities;

import com.neovisionaries.i18n.CountryCode;
import com.rocinante.shops.api.MerchantCsvUploadDto;
import com.rocinante.shops.utils.NullabilityUtils;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Type;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Setter
@Getter
@Slf4j
public class Merchant {
  @Id
  @GenericField
  private UUID uuid;

  @Column
  @FullTextField
  private String name;

  @Column
  @Type(type = "com.rocinante.shops.datastore.types.UrlType")
  private URL url;

  @Column
  @Type(type = "com.rocinante.shops.datastore.types.CountryCodeType")
  private CountryCode countryCode;

  @Column
  @GenericField
  private boolean enabled;

  @ManyToMany(mappedBy = "merchants", fetch = FetchType.LAZY)
  @IndexedEmbedded
  private Set<BnplProvider> bnplProviders = new HashSet<>();

  @OneToMany(
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY,
      mappedBy = "merchant")
  private Set<MerchantInferredCategory> merchantInferredCategories = new HashSet<>();

  @Column private OffsetDateTime createdAt;

  @Column private OffsetDateTime updatedAt;

  @Column private OffsetDateTime lastCrawledAt;

  public Merchant(MerchantCsvUploadDto merchantCsvUploadDto) throws MalformedURLException {
    this.uuid = UUID.randomUUID();
    this.name = merchantCsvUploadDto.getMerchantName();
    this.url = new URL(merchantCsvUploadDto.getMerchantWebsite());
    this.countryCode =  CountryCode.US; // TODO: Remove hardcoded value
    this.enabled = true;
    this.createdAt = Instant.now().atOffset(ZoneOffset.UTC);
    this.updatedAt = Instant.now().atOffset(ZoneOffset.UTC);
    this.lastCrawledAt = null;
  }

  public boolean applyUpdatesIfNeeded(MerchantCsvUploadDto merchantCsvUploadDto) {
    boolean updated = false;

    if (!NullabilityUtils.areObjectsEqual(this.name, merchantCsvUploadDto.getMerchantName())) {
      updated = true;
      log.info("Name change detected for merchant {} {}", this.uuid, this.getUrl());
      this.name = merchantCsvUploadDto.getMerchantName();
    }

    if (updated) {
      this.updatedAt = Instant.now().atOffset(ZoneOffset.UTC);
    }
    return updated;
  }

  @Override
  public int hashCode() {
    return Objects.hash(url.toString(), countryCode);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Merchant)) {
      return false;
    }
    Merchant other = (Merchant) obj;
    return this.url.toString().equals(other.url.toString()) &&
        this.countryCode.equals(other.countryCode);
  }
}
