package com.rocinante.shops.service;

import com.rocinante.datastore.dao.SubscriptionsDao;
import com.rocinante.datastore.entities.Subscriptions;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@AllArgsConstructor
@Slf4j
public class SubscriptionsService {
  private final SubscriptionsDao subscriptionsDao;

  public void subscribe(String email) {
    if (!EmailValidator.getInstance().isValid(email)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email Invalid");
    }
    subscriptionsDao.save(new Subscriptions(email, false));
  }
}
