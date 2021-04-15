package com.rocinante.shops.api;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ContactForm {
  private String name;
  private String email;
  private String subject;
  private String message;
}
