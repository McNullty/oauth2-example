package com.mladen.cikara.oauth2.authorization.server.security.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EntityNotFoundException extends Exception {

  /**
   *
   */
  private static final long serialVersionUID = 7044223464105217972L;

}