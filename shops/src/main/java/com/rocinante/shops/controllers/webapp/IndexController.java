package com.rocinante.shops.controllers.webapp;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor
@Slf4j
public class IndexController {
  @RequestMapping("/")
  public String index(Model model) {
    log.info("Landing Page Impression");
    model.addAttribute("query", "");
    return "index";
  }
}
