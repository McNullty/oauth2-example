package com.mladen.cikara.oauth2.authorization.server.security.model;

import org.springframework.security.core.GrantedAuthority;

/**
 * This is single place where security authorities are added. You can add new
 * Authorities but if you remove or rename existing authorty you must create
 * database migration with same effect.
 *
 * @author mladen
 *
 */
public enum Authority implements GrantedAuthority {
  ROLE_ADMIN, ROLE_SYS_ADMIN, ROLE_USER;

  @Override
  public String getAuthority() {
    return name();
  }
}
