package com.mladen.cikara.oauth2.authorization.server.security.model;

/**
 * You can't set both password or encryptedPassword in Builder. Choose one.
 *
 * @author Mladen Čikara mladen.cikara@gmail.com
 *
 */
public class DuplicatePasswordsSetException extends RuntimeException {

  /**
   *
   */
  private static final long serialVersionUID = -4024547151332900379L;

}
