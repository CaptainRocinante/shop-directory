package com.rocinante.shops.controllers.webapp;

import com.rocinante.shops.api.ContactForm;
import com.rocinante.shops.service.EmailService;
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
  private final EmailService emailService;

  @PostMapping(
      path = "/web/contact",
      consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
  public ResponseEntity<String> handleContactFormSubmission(ContactForm contactForm) {
    log.info("ContactForm {}", contactForm);
    try {
      emailService.sendEmail(contactForm.getName() + " " + contactForm.getEmail(),
          "admin@paylatergoods.com", contactForm.getSubject(),
          "admin@paylatergoods.com", contactForm.getMessage());
    } catch (Exception e) {
      log.error("Failed to send email", e);
    }

    return new ResponseEntity<>(
        "Thank you for getting in touch. We'll contact you shortly.", HttpStatus.OK);
  }
}
