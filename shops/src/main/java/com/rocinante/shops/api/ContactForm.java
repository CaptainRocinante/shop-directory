package com.rocinante.shops.api;

import lombok.Data;

@Data
public class ContactForm {
  private final String name;
  private final String email;
  private final String subject;
  private final String message;
}
