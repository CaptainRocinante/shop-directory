package com.rocinante.shops.service.sync;

import com.rocinante.shops.datastore.dao.BnplDao;
import com.rocinante.shops.datastore.entities.BnplProvider;
import java.util.List;
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
}
