package com.rocinante.shops.datastore.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Slf4j
public class Subscriptions {
  @Id
  private String email;
}
