package com.mladen.cikara.oauth2.authorization.server.security;

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

  private org.springframework.security.core.userdetails.User createSpringSecurityUser(User user) {

    final org.springframework.security.core.userdetails.User userDetails =
        new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
            user.getAuthorities());

    return userDetails;
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    final Optional<User> user = userRepository.findByEmail(email);

    return user
        .map(u -> createSpringSecurityUser(u))
        .orElseThrow(
            () -> new UsernameNotFoundException(
                "User " + email + " was not found in the database"));
  }
}
