package com.rocinante.shops.controllers.webapp;

import com.rocinante.shops.api.SubscriptionForm;
import com.rocinante.shops.service.SubscriptionsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@AllArgsConstructor
@Slf4j
public class SubscriptionsController {
  private final SubscriptionsService subscriptionsService;

  @PostMapping(
      path = "/web/subscribe",
      consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
  public ResponseEntity<String> handleSubscription(SubscriptionForm subscriptionForm) {
    log.info("SubscriptionForm {}", subscriptionForm);
    try {
      subscriptionsService.subscribe(subscriptionForm.getEmail());
    } catch (Exception e) {
      log.error("Email Subscribe threw error", e);
    }
    return new ResponseEntity<>("You are now subscribed.", HttpStatus.OK);
  }
}
