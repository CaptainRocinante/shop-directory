package com.rocinante.scraping.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Slf4j
public class PingController {
  @GetMapping("/ping")
  public String ping() {
    return "pong";
  }
}
