package com.rocinante.datastore.entities;

import javax.persistence.Column;
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
  @Id private String email;

  @Column private boolean verified;
}
