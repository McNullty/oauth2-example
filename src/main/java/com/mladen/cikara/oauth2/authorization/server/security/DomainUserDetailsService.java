package com.mladen.cikara.oauth2.authorization.server.security;

import com.mladen.cikara.oauth2.authorization.server.security.model.SpringSecurityUserAdapter;
import com.mladen.cikara.oauth2.authorization.server.security.model.User;
import com.mladen.cikara.oauth2.authorization.server.security.repository.UserRepository;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * Authenticate a user from the database.
 *
 * @author mladen
 *
 */
@Component("userDetailsService")
public class DomainUserDetailsService implements UserDetailsService {

  @Autowired
  private UserRepository userRepository;

  private UserDetails createSpringSecurityUser(final User user) {

    final SpringSecurityUserAdapter userDetails =
        new SpringSecurityUserAdapter(user);

    return userDetails;
  }

  @Override
  public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
    final Optional<User> user = this.userRepository.findByEmail(email);

    return user
        .map(u -> createSpringSecurityUser(u))
        .orElseThrow(
            () -> new UsernameNotFoundException(
                "User " + email + " was not found in the database"));
  }
}
