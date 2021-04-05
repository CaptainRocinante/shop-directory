package com.rocinante.shops.datastore.entities;

import com.rocinante.crawlers.category.Category;
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
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
public class MerchantInferredCategory {
  @Id private UUID uuid;

  @Column
  @FullTextField
  private String name;

  @Column
  @Type(type = "com.rocinante.shops.datastore.types.UrlType")
  private URL url;

  @Column private boolean enabled;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "merchant_uuid")
  @IndexedEmbedded
  private Merchant merchant;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "product_inferred_category_mapping",
      joinColumns = {@JoinColumn(name = "merchant_inferred_category_uuid")},
      inverseJoinColumns = {@JoinColumn(name = "product_uuid")})
  private Set<Product> products = new HashSet<>();

  @Column private OffsetDateTime createdAt;

  @Column private OffsetDateTime updatedAt;

  @Column private OffsetDateTime lastCrawledAt;

  @Override
  public int hashCode() {
    return Objects.hash(uuid, url.toString());
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof MerchantInferredCategory)) {
      return false;
    }
    MerchantInferredCategory other = (MerchantInferredCategory) obj;
    return this.uuid.equals(other.uuid) && this.url.toString().equals(other.url.toString());
  }

  public MerchantInferredCategory(Merchant merchant, Category category)
      throws MalformedURLException {
    this(
        UUID.randomUUID(),
        StringUtils.left(category.getCategoryName(), 128),
        new URL(category.getCategoryUrl()),
        true,
        merchant,
        new HashSet<>(),
        Instant.now().atOffset(ZoneOffset.UTC),
        Instant.now().atOffset(ZoneOffset.UTC),
        null);
  }

  public boolean applyUpdatesIfNeeded(Category category) {
    boolean updated = false;
    if (!NullabilityUtils.areObjectsEqual(
        this.name, StringUtils.left(category.getCategoryName(), 128))) {
      updated = true;
      this.name = StringUtils.left(category.getCategoryName(), 128);
    }
    if (updated) {
      this.updatedAt = Instant.now().atOffset(ZoneOffset.UTC);
    }
    return updated;
  }

  public void addProduct(Product product) {
    this.products.add(product);
    // Following line commented out for efficiency, we'll let the category handle updates to the
    // mapping table instead of the product
    // product.getMerchantInferredCategories().add(this);
  }

  public void removeProduct(Product product) {
    this.products.remove(product);
    // Following line commented out for efficiency, we'll let the category handle updates to the
    // mapping table instead of the product
    // product.getMerchantInferredCategories().remove(this);
  }
}
