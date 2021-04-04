package com.rocinante.shops.datastore.entities;

import com.rocinante.shops.api.BnplCsvUploadDto;
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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Type;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Slf4j
public class BnplProvider {
  @Id private UUID uuid;

  @Column private String name;

  @Column
  @Type(type = "com.rocinante.shops.datastore.types.UrlType")
  private URL url;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "bnpl_provider_merchant_mapping",
      joinColumns = {@JoinColumn(name = "bnpl_provider_uuid")},
      inverseJoinColumns = {@JoinColumn(name = "merchant_uuid")})
  private Set<Merchant> merchants = new HashSet<>();

  @Column private OffsetDateTime createdAt;

  @Column private OffsetDateTime updatedAt;

  public BnplProvider(BnplCsvUploadDto bnplCsvUploadDto) throws MalformedURLException {
    this.uuid = UUID.randomUUID();
    this.name = bnplCsvUploadDto.getBnplProviderName();
    this.url = new URL(bnplCsvUploadDto.getBnplProviderWebsite());
    this.createdAt = Instant.now().atOffset(ZoneOffset.UTC);
    this.updatedAt = Instant.now().atOffset(ZoneOffset.UTC);
  }

  public boolean applyUpdatesIfNeeded(BnplCsvUploadDto bnplCsvUploadDto) {
    boolean updated = false;
    if (!NullabilityUtils.areObjectsEqual(this.name, bnplCsvUploadDto.getBnplProviderName())) {
      log.info("Name updated for Bnpl provider {} {}", this.uuid, this.url);
      updated = true;
      this.name = bnplCsvUploadDto.getBnplProviderName();
    }
    if (updated) {
      this.updatedAt = Instant.now().atOffset(ZoneOffset.UTC);
    }
    return updated;
  }

  @Override
  public int hashCode() {
    return Objects.hash(url);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof BnplProvider)) {
      return false;
    }
    BnplProvider other = (BnplProvider) obj;
    return this.url.equals(other.url);
  }

  public void addMerchant(Merchant merchant) {
    this.merchants.add(merchant);
  }

  public void removeMerchant(Merchant merchant) {
    this.merchants.remove(merchant);
  }
}
