package com.mladen.cikara.oauth2.authorization.server.security.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.NotEmpty;

@JsonRootName("userAuthorities")
public class AuthorityDto {

  @NotEmpty
  private final Set<Authority> authorities;

  @JsonCreator
  public AuthorityDto(
      @NotEmpty
      @JsonProperty("authorities")
      final Set<Authority> authorities) {
    this.authorities  = new HashSet<>();
    this.authorities.addAll(authorities);
  }

  public Set<Authority> getAuthorities() {
    return this.authorities;
  }

  @Override
  public String toString() {
    return "AuthorityDto [authorities=" + this.authorities + "]";
  }

}
