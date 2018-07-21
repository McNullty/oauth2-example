package com.mladen.cikara.oauth2.authorization.server.security.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class WrongPasswordsException extends RuntimeException {

  private static final long serialVersionUID = 7142178498433556438L;

  public WrongPasswordsException(final String message) {
    super(message);
  }
}
