package com.rocinante.shops.datastore.entities;

import com.neovisionaries.i18n.CurrencyCode;
import com.rocinante.crawlers.summary.ProductSummary;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
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

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "merchant_inferred_category_uuid")
  private MerchantInferredCategory merchantInferredCategory;

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

  public Product(ProductSummary productSummary, MerchantInferredCategory merchantInferredCategory)
      throws MalformedURLException {
    this(
        UUID.randomUUID(),
        productSummary.getInferredDescription(),
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
    this.name = productSummary.getInferredDescription();
    this.merchantInferredCategory = merchantInferredCategory;
    this.currencyCode = CurrencyCode.getByCode(productSummary.getCurrency());
    this.currentPriceLowerRange = productSummary.currentPriceLowerRange();
    this.currentPriceUpperRange = productSummary.currentPriceUpperRange();
    this.originalPriceLowerRange = productSummary.originalPriceLowerRange();
    this.originalPriceUpperRange = productSummary.originalPriceUpperRange();
    this.mainImageUrl = new URL(productSummary.mainProductImage());
    this.updatedAt = Instant.now().atOffset(ZoneOffset.UTC);
  }
}
