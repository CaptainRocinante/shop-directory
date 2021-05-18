package com.rocinante.scraping.service.sync;

import com.rocinante.common.api.dto.MerchantCreateDto;
import com.rocinante.common.api.dto.MerchantCrudDto;
import com.rocinante.datastore.dao.BnplDao;
import com.rocinante.datastore.dao.MerchantDao;
import com.rocinante.datastore.entities.BnplProvider;
import com.rocinante.datastore.entities.Merchant;
import com.rocinante.datastore.entities.MerchantInferredCategory;
import com.rocinante.scraping.service.async.AsyncIndexingService;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class MerchantService {
  private final BnplDao bnplDao;
  private final MerchantDao merchantDao;
  private final AsyncIndexingService asyncIndexingService;

  @Transactional
  public void updateMerchant(MerchantCrudDto merchantCrudDto) {
    final Merchant merchant = merchantDao.getOne(UUID.fromString(merchantCrudDto.getUuid()));
    if (merchant.applyUpdatesIfNeeded(merchantCrudDto)) {
      merchantDao.saveAndFlush(merchant);
      // Manually trigger async indexing of all the products of the merchant
      final Set<MerchantInferredCategory> merchantInferredCategorySet =
          merchant.getMerchantInferredCategories();
      for (final MerchantInferredCategory merchantInferredCategory : merchantInferredCategorySet) {
        asyncIndexingService.reindexAllProductsForMerchantInferredCategory(
            merchantInferredCategory.getUuid());
      }
    }
  }

  @Transactional
  public String createMerchant(MerchantCreateDto merchantCreateDto) throws MalformedURLException {
    final BnplProvider bnplProvider =
        bnplDao.findById(UUID.fromString(merchantCreateDto.getBnplUuid())).orElseThrow();
    final URL merchantUrl = new URL(merchantCreateDto.getMerchantUrl());
    final Optional<Merchant> existingMerchant = merchantDao.findByUrl(merchantUrl);
    if (existingMerchant.isPresent()) {
      throw new IllegalStateException(String.format("Merchant with website %s already exists",
          merchantUrl));
    }
    final Merchant merchant = merchantDao.save(new Merchant(merchantCreateDto));
    bnplProvider.addMerchant(merchant);
    bnplDao.save(bnplProvider);
    return merchant.getUuid().toString();
  }
}
