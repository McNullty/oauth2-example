package com.mladen.cikara.oauth2.resource.server.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PasswordsDontMatchException extends RuntimeException {

  private static final long serialVersionUID = 925987266417146089L;

  public PasswordsDontMatchException(String message) {
    super(message);
  }
}
