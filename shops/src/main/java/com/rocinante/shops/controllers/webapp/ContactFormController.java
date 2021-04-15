package com.rocinante.shops.controllers.webapp;

import com.rocinante.shops.api.ContactForm;
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
public class ContactFormController {
  @PostMapping(
      path = "/web/contact",
      consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
  public ResponseEntity<String> handleContactFormSubmission(ContactForm contactForm) {
    log.info("ContactForm {}", contactForm);
    return new ResponseEntity<String>(
        "Thank you for getting in touch. We'll contact you shortly.",
        HttpStatus.OK);
  }
}
