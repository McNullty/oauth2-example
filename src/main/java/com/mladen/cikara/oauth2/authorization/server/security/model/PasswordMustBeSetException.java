package com.mladen.cikara.oauth2.authorization.server.security.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * You can set it via User.Builder password or encryptedPassword
 *
 * @author mladen
 *
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PasswordMustBeSetException extends RuntimeException {

  private static final long serialVersionUID = -3182632265531382054L;

  public PasswordMustBeSetException(final String message) {
    super(message);
  }
}
