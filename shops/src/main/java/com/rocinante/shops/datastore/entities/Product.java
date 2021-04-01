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
import org.flywaydb.core.internal.util.StringUtils;
import org.hibernate.annotations.Type;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
public class Product {
  @Id
  private UUID uuid;

  @Column
  private String name;

  @ManyToMany(fetch = FetchType.EAGER, mappedBy = "products")
  private Set<MerchantInferredCategory> merchantInferredCategories = new HashSet<>();

  @Column
  @Type(type = "com.rocinante.shops.datastore.types.UrlType")
  private URL url;

  @Column
  private boolean enabled;

  @Type(type = "com.rocinante.shops.datastore.types.CurrencyCodeType")
  private CurrencyCode currencyCode;

  @Column
  private BigDecimal currentPriceLowerRange;

  @Column
  private BigDecimal currentPriceUpperRange;

  @Column
  private BigDecimal originalPriceLowerRange;

  @Column
  private BigDecimal originalPriceUpperRange;

  @Column
  @Type(type = "com.rocinante.shops.datastore.types.UrlType")
  private URL mainImageUrl;

  @Column
  private OffsetDateTime createdAt;

  @Column
  private OffsetDateTime updatedAt;

  @Column
  private OffsetDateTime lastCrawledAt;

  @Override
  public int hashCode() {
    return Objects.hash(url);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Product)) {
      return false;
    }
    Product other = (Product) obj;
    return this.url.equals(other.url);
  }

  private Product(UUID uuid, String name,
      MerchantInferredCategory merchantInferredCategory, URL url, boolean enabled,
      CurrencyCode currencyCode, BigDecimal currentPriceLowerRange,
      BigDecimal currentPriceUpperRange, BigDecimal originalPriceLowerRange,
      BigDecimal originalPriceUpperRange, URL mainImageUrl, OffsetDateTime createdAt,
      OffsetDateTime updatedAt, OffsetDateTime lastCrawledAt) {
    this.uuid = uuid;
    this.name = name;
    this.merchantInferredCategories.add(merchantInferredCategory);
    this.url = url;
    this.enabled = enabled;
    this.currencyCode = currencyCode;
    this.currentPriceLowerRange = currentPriceLowerRange;
    this.currentPriceUpperRange = currentPriceUpperRange;
    this.originalPriceLowerRange = originalPriceLowerRange;
    this.originalPriceUpperRange = originalPriceUpperRange;
    this.mainImageUrl = mainImageUrl;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.lastCrawledAt = lastCrawledAt;
  }

  public boolean isSameAsLastCrawl(
      ProductSummary productSummary,
      MerchantInferredCategory merchantInferredCategory) throws MalformedURLException {
    return StringUtils.left(productSummary.getInferredDescription(), 128).equals(name) &&
        NullabilityUtils.equals(CurrencyCode.getByCode(productSummary.getCurrency()), currencyCode) &&
        NullabilityUtils.equals(productSummary.currentPriceLowerRange(), currentPriceLowerRange) &&
        NullabilityUtils.equals(productSummary.currentPriceUpperRange(), currentPriceUpperRange) &&
        NullabilityUtils.equals(productSummary.originalPriceLowerRange(),
            originalPriceLowerRange) &&
        NullabilityUtils.equals(productSummary.originalPriceUpperRange(),
            originalPriceUpperRange) &&
        NullabilityUtils.equals(new URL(productSummary.mainProductImage()), mainImageUrl) &&
        merchantInferredCategories.contains(merchantInferredCategory);
  }

  public Product(ProductSummary productSummary, MerchantInferredCategory merchantInferredCategory)
      throws MalformedURLException {
    this(
        UUID.randomUUID(),
        StringUtils.left(productSummary.getInferredDescription(), 128),
        merchantInferredCategory,
        new URL(productSummary.getUrl()),
        true,
        CurrencyCode.getByCode(productSummary.getCurrency()),
        productSummary.currentPriceLowerRange(),
        productSummary.currentPriceUpperRange(),
        productSummary.originalPriceLowerRange(),
        productSummary.originalPriceLowerRange(),
        new URL(productSummary.mainProductImage()),
        Instant.now().atOffset(ZoneOffset.UTC),
        Instant.now().atOffset(ZoneOffset.UTC),
        null
    );
  }

  public void updateFromLatestCrawl(ProductSummary productSummary,
      MerchantInferredCategory merchantInferredCategory) throws MalformedURLException {
    this.name = StringUtils.left(productSummary.getInferredDescription(), 128);
    this.merchantInferredCategories.add(merchantInferredCategory);
    this.currencyCode = CurrencyCode.getByCode(productSummary.getCurrency());
    this.currentPriceLowerRange = productSummary.currentPriceLowerRange();
    this.currentPriceUpperRange = productSummary.currentPriceUpperRange();
    this.originalPriceLowerRange = productSummary.originalPriceLowerRange();
    this.originalPriceUpperRange = productSummary.originalPriceUpperRange();
    this.mainImageUrl = new URL(productSummary.mainProductImage());
    this.updatedAt = Instant.now().atOffset(ZoneOffset.UTC);
  }
}
