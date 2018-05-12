package com.mladen.cikara.oauth2.resource.server.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

  @RequestMapping("/public")
  public String privateHome() {
    return "Hello from the public side";
  }

  @RequestMapping("/private")
  public String publicHome() {
    return "Hello from the private side";
  }
}
