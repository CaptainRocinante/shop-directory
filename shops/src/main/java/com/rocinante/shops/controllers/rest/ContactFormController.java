package com.rocinante.shops.controllers.rest;

import com.rocinante.shops.api.ContactForm;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class ContactFormController {

  @PostMapping(
      path = "/contact",
      consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
  public void handleContactFormSubmission(ContactForm contactForm) {

  }
}
