package com.mladen.cikara.oauth2.authorization.server.security.model;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;

/**
 * This is single place where security authorities are added. You can add new Authorities but if you
 * remove or rename existing authorty you must create database migration with same effect.
 *
 * @author mladen
 *
 */
public enum Authority implements GrantedAuthority {
  ROLE_ADMIN, ROLE_SYS_ADMIN, ROLE_USER;

  private static Map<String, Authority> namesMap = new HashMap<>();

  static {
    namesMap.put("ROLE_ADMIN", ROLE_ADMIN);
    namesMap.put("ROLE_SYS_ADMIN", ROLE_SYS_ADMIN);
    namesMap.put("ROLE_USER", ROLE_USER);
  }

  @JsonCreator
  public static Authority forValue(final String value) {
    return namesMap.get(value.toUpperCase());
  }

  @Override
  public String getAuthority() {
    return name();
  }
}
