package com.mladen.cikara.oauth2.authorization.server.security.model;

/**
 * You can't set both password or encryptedPassword in Builder. Choose one.
 *
 * @author mladen
 *
 */
public class DuplicatePasswordsSetException extends RuntimeException {

  private static final long serialVersionUID = -4024547151332900379L;

  public DuplicatePasswordsSetException(final String message) {
    super(message);
  }
}
