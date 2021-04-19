package com.rocinante.shops.service;

import com.rocinante.datastore.dao.MerchantDao;
import com.rocinante.datastore.entities.Merchant;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class MerchantService {
  private final MerchantDao merchantDao;

  @Transactional(readOnly = true)
  public List<Merchant> getAllMerchantsForUuids(List<UUID> merchantUuids) {
    return merchantDao.findAllById(merchantUuids);
  }
}
