package com.rocinante.shops.datastore.entities;

import com.neovisionaries.i18n.CurrencyCode;
import java.math.BigDecimal;
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
public class Product {
  @Id
  private UUID uuid;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "merchant_uuid")
  private Merchant merchant;

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
  private OffsetDateTime lastCrawledAt;
}
