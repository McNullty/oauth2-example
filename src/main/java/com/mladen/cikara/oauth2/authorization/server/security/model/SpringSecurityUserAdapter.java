package com.mladen.cikara.oauth2.authorization.server.security.model;

import java.util.Collection;

import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Custom implementation of {@link org.springframework.security.core.userdetails.User}.
 *
 * @author mladen
 *
 */
public class SpringSecurityUserAdapter
    implements UserDetails, CredentialsContainer {

  private static final long serialVersionUID = 2727303042199106548L;

  private final User user;
  private final boolean accountNonExpired;
  private final boolean accountNonLocked;
  private final boolean credentialsNonExpired;
  private final boolean enabled;

  /**
   * Creates object that implements UserDetals interface that is used by Spring Security.
   *
   * @param user
   *          Domain User object
   */
  public SpringSecurityUserAdapter(final User user) {
    this.user = user;
    this.enabled = true;
    this.accountNonExpired = true;
    this.credentialsNonExpired = true;
    this.accountNonLocked = true;
  }

  @Override
  public void eraseCredentials() {
    // TODO: check if this can be empty. User password should be hidden by default
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return this.user.getAuthorities();
  }

  @Override
  public String getPassword() {
    return this.user.getPassword();
  }

  public User getUser() {
    return this.user;
  }

  @Override
  public String getUsername() {
    return this.user.getEmail();
  }

  @Override
  public boolean isAccountNonExpired() {
    return this.accountNonExpired;
  }

  @Override
  public boolean isAccountNonLocked() {
    return this.accountNonLocked;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return this.credentialsNonExpired;
  }

  @Override
  public boolean isEnabled() {
    return this.enabled;
  }

  @Override
  public String toString() {
    return "SpringSecurityUserAdapter [user=" + this.user + ", accountNonExpired="
        + this.accountNonExpired
        + ", accountNonLocked=" + this.accountNonLocked + ", credentialsNonExpired="
        + this.credentialsNonExpired + ", enabled=" + this.enabled + "]";
  }
}
