package com.rocinante.shops.service.sync;

import com.rocinante.shops.datastore.dao.BnplDao;
import com.rocinante.shops.datastore.entities.BnplProvider;
import com.rocinante.shops.datastore.entities.Merchant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class BnplService {
  private final BnplDao bnplDao;

  @Transactional(readOnly = true)
  public List<BnplProvider> getAllBnplProviders() {
    return bnplDao.findAll();
  }

  @Transactional(readOnly = true)
  public List<Merchant> getAllMerchantsForBnplProvider(UUID bnplUuid) {
    return bnplDao.findById(bnplUuid).stream()
        .map(BnplProvider::getMerchants)
        .map(Set::stream)
        .flatMap(Function.identity())
        .collect(Collectors.toList());
  }
}
