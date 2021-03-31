package com.rocinante.shops.datastore.entities;

import com.neovisionaries.i18n.CurrencyCode;
import com.rocinante.crawlers.summary.ProductSummary;
import java.math.BigDecimal;
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
  private String url;

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
  private OffsetDateTime createdAt;

  @Column
  private OffsetDateTime updatedAt;

  @Column
  private OffsetDateTime lastCrawledAt;

  public Product(ProductSummary productSummary, MerchantInferredCategory merchantInferredCategory) {
    this(
        UUID.randomUUID(),
        productSummary.getInferredDescription(),
        merchantInferredCategory,
        productSummary.getUrl(),
        true,
        CurrencyCode.getByCode(productSummary.getCurrency()),
        productSummary.currentPriceLowerRange(),
        productSummary.currentPriceUpperRange(),
        productSummary.originalPriceLowerRange(),
        productSummary.originalPriceLowerRange(),
        Instant.now().atOffset(ZoneOffset.UTC),
        Instant.now().atOffset(ZoneOffset.UTC),
        null
    );
  }
}
