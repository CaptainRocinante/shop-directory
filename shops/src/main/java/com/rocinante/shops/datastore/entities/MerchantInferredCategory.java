package com.rocinante.shops.datastore.entities;

import com.rocinante.crawlers.category.Category;
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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
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
public class MerchantInferredCategory {
  @Id
  private UUID uuid;

  @Column
  private String name;

  @Column
  @Type(type = "com.rocinante.shops.datastore.types.UrlType")
  private URL url;

  @Column
  private boolean enabled;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "merchant_uuid")
  private Merchant merchant;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "product_inferred_category_mapping",
      joinColumns = { @JoinColumn(name = "merchant_inferred_category_uuid") },
      inverseJoinColumns = { @JoinColumn(name = "product_uuid") }
  )
  private Set<Product> products = new HashSet<>();

  @Column
  private OffsetDateTime createdAt;

  @Column
  private OffsetDateTime updatedAt;

  @Column
  private OffsetDateTime lastCrawledAt;

  @Override
  public int hashCode() {
    return Objects.hash(uuid, url);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof MerchantInferredCategory)) {
      return false;
    }
    MerchantInferredCategory other = (MerchantInferredCategory) obj;
    return this.uuid.equals(other.uuid) && this.url.equals(other.url);
  }

  public MerchantInferredCategory(Merchant merchant, Category category)
      throws MalformedURLException {
    this(UUID.randomUUID(),
        StringUtils.left(category.getCategoryName(), 128),
        new URL(category.getCategoryUrl()),
        true,
        merchant,
        new HashSet<>(),
        Instant.now().atOffset(ZoneOffset.UTC),
        Instant.now().atOffset(ZoneOffset.UTC),
        null
    );
  }

  public void updateFromLatestCrawl(Merchant merchant, Category category) {
    this.name = StringUtils.left(category.getCategoryName(), 128);
    this.merchant = merchant;
    this.updatedAt = Instant.now().atOffset(ZoneOffset.UTC);
  }

  public void addProduct(Product product) {
    this.products.add(product);
    product.getMerchantInferredCategories().add(this);
  }

  public void removeProduct(Product product) {
    this.products.remove(product);
    product.getMerchantInferredCategories().remove(this);
  }
}
