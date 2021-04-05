package com.rocinante.shops.datastore.entities;

import com.neovisionaries.i18n.CurrencyCode;
import com.rocinante.crawlers.summary.ProductSummary;
import com.rocinante.shops.utils.NullabilityUtils;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.internal.util.StringUtils;
import org.hibernate.annotations.Type;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Slf4j
@Indexed
public class Product {
  @Id private UUID uuid;

  @Column
  @FullTextField
  private String name;

  @ManyToMany(mappedBy = "products", fetch = FetchType.LAZY)
  @IndexedEmbedded
  private Set<MerchantInferredCategory> merchantInferredCategories = new HashSet<>();

  @Column
  @Type(type = "com.rocinante.shops.datastore.types.UrlType")
  private URL url;

  @Column
  @GenericField
  private boolean enabled;

  @Type(type = "com.rocinante.shops.datastore.types.CurrencyCodeType")
  private CurrencyCode currencyCode;

  @Column
  @GenericField
  private BigDecimal currentPriceLowerRange;

  @Column private BigDecimal currentPriceUpperRange;

  @Column private BigDecimal originalPriceLowerRange;

  @Column private BigDecimal originalPriceUpperRange;

  @Column
  @Type(type = "com.rocinante.shops.datastore.types.UrlType")
  private URL mainImageUrl;

  @Column private OffsetDateTime createdAt;

  @Column private OffsetDateTime updatedAt;

  @Column private OffsetDateTime lastCrawledAt;

  @Override
  public int hashCode() {
    return Objects.hash(url.toString());
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Product)) {
      return false;
    }
    Product other = (Product) obj;
    return this.url.toString().equals(other.url.toString());
  }

  public Product(ProductSummary productSummary) throws MalformedURLException {
    this.uuid = UUID.randomUUID();
    this.name = StringUtils.left(productSummary.getInferredDescription(), 128);
    this.url = new URL(productSummary.getUrl());
    this.enabled = true;
    this.currencyCode = CurrencyCode.getByCode(productSummary.getCurrency());
    this.currentPriceLowerRange = productSummary.currentPriceLowerRange();
    this.currentPriceUpperRange = productSummary.currentPriceUpperRange();
    this.originalPriceLowerRange = productSummary.originalPriceLowerRange();
    this.originalPriceUpperRange = productSummary.originalPriceUpperRange();
    this.mainImageUrl = new URL(productSummary.mainProductImage());
    this.createdAt = Instant.now().atOffset(ZoneOffset.UTC);
    this.updatedAt = Instant.now().atOffset(ZoneOffset.UTC);
    this.lastCrawledAt = null;
  }

  public boolean applyUpdatesIfNeeded(ProductSummary productSummary) throws MalformedURLException {
    boolean updated = false;
    if (!NullabilityUtils.areObjectsEqual(
        this.name, StringUtils.left(productSummary.getInferredDescription(), 128))) {
      log.info("Name has change for {} {}", this.uuid, this.url);
      updated = true;
      this.name = StringUtils.left(productSummary.getInferredDescription(), 128);
    }
    if (!NullabilityUtils.areObjectsEqual(
        this.currencyCode, CurrencyCode.getByCode(productSummary.getCurrency()))) {
      log.info("CurrencyCode has change for {} {}", this.uuid, this.url);
      updated = true;
      this.currencyCode = CurrencyCode.getByCode(productSummary.getCurrency());
    }
    if (!NullabilityUtils.areObjectsEqual(
        this.currentPriceLowerRange, productSummary.currentPriceLowerRange())) {
      log.info("CurrentPriceLowerRange has change for {} {}", this.uuid, this.url);
      updated = true;
      this.currentPriceLowerRange = productSummary.currentPriceLowerRange();
    }
    if (!NullabilityUtils.areObjectsEqual(
        this.currentPriceUpperRange, productSummary.currentPriceUpperRange())) {
      log.info("CurrentPriceUpperRange has change for {} {}", this.uuid, this.url);
      updated = true;
      this.currentPriceUpperRange = productSummary.currentPriceUpperRange();
    }
    if (!NullabilityUtils.areObjectsEqual(
        this.originalPriceLowerRange, productSummary.originalPriceLowerRange())) {
      log.info("OriginalPriceLowerRange has change for {} {}", this.uuid, this.url);
      updated = true;
      this.originalPriceLowerRange = productSummary.originalPriceLowerRange();
    }
    if (!NullabilityUtils.areObjectsEqual(
        this.originalPriceUpperRange, productSummary.originalPriceUpperRange())) {
      log.info("OriginalPriceUpperRange has change for {} {}", this.uuid, this.url);
      updated = true;
      this.originalPriceUpperRange = productSummary.originalPriceUpperRange();
    }
    if (!NullabilityUtils.areObjectsEqual(
        this.mainImageUrl.toString(), productSummary.mainProductImage())) {
      log.info("MainImage has change for {} {}", this.uuid, this.url);
      updated = true;
      this.mainImageUrl = new URL(productSummary.mainProductImage());
    }
    if (updated) {
      this.updatedAt = Instant.now().atOffset(ZoneOffset.UTC);
    }
    return updated;
  }
}
