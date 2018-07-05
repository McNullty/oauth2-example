package com.mladen.cikara.oauth2.authorization.server.security.model;

import java.util.Collection;

import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Custom implementation of
 * {@link org.springframework.security.core.userdetails.User}
 *
 * @author Mladen Čikara mladen.cikara@gmail.com
 *
 */
public class SpringSecurityUserAdapter
    implements UserDetails, CredentialsContainer {

  /**
   *
   */
  private static final long serialVersionUID = 2727303042199106548L;

  private final User user;
  private final boolean accountNonExpired;
  private final boolean accountNonLocked;
  private final boolean credentialsNonExpired;
  private final boolean enabled;

  public SpringSecurityUserAdapter(User user) {
    this.user = user;
    enabled = true;
    accountNonExpired = true;
    credentialsNonExpired = true;
    accountNonLocked = true;
  }

  @Override
  public void eraseCredentials() {
    // TODO: check if this can be empty. User password should be hidden by default
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return user.getAuthorities();
  }

  @Override
  public String getPassword() {
    return user.getPassword();
  }

  public User getUser() {
    return user;
  }

  @Override
  public String getUsername() {
    return user.getEmail();
  }

  @Override
  public boolean isAccountNonExpired() {
    return accountNonExpired;
  }

  @Override
  public boolean isAccountNonLocked() {
    return accountNonLocked;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return credentialsNonExpired;
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }

  @Override
  public String toString() {
    return "SpringSecurityUserAdapter [user=" + user + ", accountNonExpired=" + accountNonExpired
        + ", accountNonLocked=" + accountNonLocked + ", credentialsNonExpired="
        + credentialsNonExpired + ", enabled=" + enabled + "]";
  }
}
