package com.rocinante.shops.datastore.entities;

import com.rocinante.crawlers.category.Category;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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

  @OneToMany(fetch =  FetchType.LAZY, mappedBy = "merchantInferredCategory")
  private Set<Product> products = new HashSet<>();

  @Column
  private OffsetDateTime createdAt;

  @Column
  private OffsetDateTime updatedAt;

  @Column
  private OffsetDateTime lastCrawledAt;

  public MerchantInferredCategory(Merchant merchant, Category category)
      throws MalformedURLException {
    this(UUID.randomUUID(),
        category.getCategoryName(),
        new URL(category.getCategoryUrl()),
        true,
        merchant,
        new HashSet<>(),
        Instant.now().atOffset(ZoneOffset.UTC),
        Instant.now().atOffset(ZoneOffset.UTC),
        null
    );
  }
}
