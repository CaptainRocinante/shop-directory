package com.rocinante.shops.service;

import com.opencsv.bean.CsvToBeanBuilder;
import com.rocinante.shops.api.BnplCsvUploadDto;
import com.rocinante.shops.api.MerchantCsvUploadDto;
import com.rocinante.shops.datastore.dao.BnplDao;
import com.rocinante.shops.datastore.dao.MerchantDao;
import com.rocinante.shops.datastore.entities.BnplProvider;
import com.rocinante.shops.datastore.entities.Merchant;
import com.rocinante.shops.utils.ResourceUtils;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Slf4j
public class AsyncBnplMerchantUploadService {
  private static final String BNPL_FILE_PATH = "bnpls/bnpls.csv";
  private final BnplDao bnplDao;
  private final MerchantDao merchantDao;

  @Async
  @Transactional
  public void setupBnplsAndUploadMerchantsIdempotent() throws FileNotFoundException {
    final List<BnplCsvUploadDto> bnpls =
        new CsvToBeanBuilder<BnplCsvUploadDto>(
            new FileReader(ResourceUtils.getFileAtPath(BNPL_FILE_PATH)))
            .withType(BnplCsvUploadDto.class)
            .build()
            .parse();
    log.info("Count of Bnpl providers in CSV: {}", bnpls.size());

    bnpls.forEach(bnplDto -> {
      final URL url;
      try {
        url = new URL(bnplDto.getBnplProviderWebsite());
        final Optional<BnplProvider> existingProvider = bnplDao.findByUrl(url);
        final BnplProvider bnplProvider;
        if (existingProvider.isPresent()) {
          bnplProvider = existingProvider.get();
          log.info("Existing Bnpl Provider found in DB {} {}", bnplProvider.getUuid(),
              bnplProvider.getUrl());
          boolean updated = bnplProvider.applyUpdatesIfNeeded(bnplDto);
          if (updated) {
            log.info("Update detected for Bnpl Provider, saving to DB {} {}",
                bnplProvider.getUuid(), bnplProvider.getUrl());
            bnplDao.save(bnplProvider);
          }
        } else {
          bnplProvider = bnplDao.save(new BnplProvider(bnplDto));
          log.info("New Bnpl Provider created in DB {} {}", bnplProvider.getUuid(),
              bnplProvider.getUrl());
        }
        setupMerchantsForBnplIdempotent(bnplProvider);
      } catch (MalformedURLException | FileNotFoundException e) {
        throw new RuntimeException(e);
      }
    });
  }

  private void setupMerchantsForBnplIdempotent(BnplProvider bnplProvider)
      throws FileNotFoundException, MalformedURLException {
    final String merchantCsvPath = "merchants/" + bnplProvider.getName().toLowerCase() + ".csv";
    final List<MerchantCsvUploadDto> merchants =
        new CsvToBeanBuilder<MerchantCsvUploadDto>(
            new FileReader(ResourceUtils.getFileAtPath(merchantCsvPath)))
            .withType(MerchantCsvUploadDto.class)
            .build()
            .parse();

    log.info("Count of merchants in CSV for BNPL {} {} is {}", bnplProvider.getUuid(),
        bnplProvider.getUrl(), merchants.size());

    for (final MerchantCsvUploadDto merchantCsvUploadDto : merchants) {
      final URL merchantUrl = new URL(merchantCsvUploadDto.getMerchantWebsite());
      final Optional<Merchant> existingMerchant = merchantDao.findByUrl(merchantUrl);
      final Merchant merchant;
      if (existingMerchant.isPresent()) {
        merchant = existingMerchant.get();
        log.info("Existing Merchant found in DB {} {}", merchant.getUuid(), merchant.getUrl());
        boolean updated = merchant.applyUpdatesIfNeeded(merchantCsvUploadDto);
        if (updated) {
          log.info("Update detected for merchant, saving to DB {} {}", merchant.getUuid(),
              merchant.getUrl());
          merchantDao.save(merchant);
        }
      } else {
        merchant = merchantDao.save(new Merchant(merchantCsvUploadDto));
        log.info("New Merchant created in DB {} {}", merchant.getUuid(), merchant.getUrl());
      }
      bnplProvider.addMerchant(merchant);
    }
    bnplDao.save(bnplProvider);
    log.info("End Merchant CSV upload for BNPL {} {}", bnplProvider.getUuid(),
        bnplProvider.getUrl());
  }
}
