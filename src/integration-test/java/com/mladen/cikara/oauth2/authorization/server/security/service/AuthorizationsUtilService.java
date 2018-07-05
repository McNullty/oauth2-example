package com.mladen.cikara.oauth2.authorization.server.security.service;

import com.mladen.cikara.oauth2.authorization.server.security.model.Authority;
import com.mladen.cikara.oauth2.authorization.server.security.model.User;
import com.mladen.cikara.oauth2.authorization.server.security.repository.UserRepository;
import com.mladen.cikara.oauth2.util.OAuth2AuthorizationBuilder;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.MockMvc;

@Service
public class AuthorizationsUtilService {

  private static final String PASSWORD = "secret";
  private final UserRepository userRepository;
  private final MockMvc mockMvc;

  /**
   * Initializes service
   *
   * @param userService
   * @param userRepositiry
   */
  public AuthorizationsUtilService(UserRepository userRepository, MockMvc mockMvc) {
    this.userRepository = userRepository;
    this.mockMvc = mockMvc;
  }

  /**
   * Creates temporary user with specified authorities
   *
   * @param authorities
   *          Array of authorities that temp user should have
   *
   * @return temp user that should be used for testing
   */
  public User createTempUserWithAuthorities(Authority... authorities) {
    User user = new User.Builder()
        .email("temp.user@oauth2.com")
        .password(PASSWORD)
        .firstName("Temp")
        .lastName("User")
        .authorities(authorities).build();

    user = userRepository.save(user);

    return user;
  }

  /**
   * Returns user with only ADMIN_ROLE (admin@oauth2.com)
   *
   * @return user with email admin@oauth2.com and ADMIN_ROLE
   *
   * @throws NoSuchElementException
   */
  public User getAdminUser() {
    final Optional<User> user = userRepository.findByEmail("admin@oauth2.com");

    return user.get();
  }

  /**
   * Returns JWT token for given user. Password is hard coded to string "secret"
   * so user that is passed in should be created whit methods
   * {@link #getBasicUser()} {@link #getAdminUser()} or
   * {@link #getAuthorizationJWT(User)}
   *
   * @param user
   *          User for which JTW is created
   * @return JWT token
   * @throws Exception
   */
  public String getAuthorizationJWT(User user) throws Exception {
    // @formatter:off
    final String jwt =
        OAuth2AuthorizationBuilder
          .oauth2Request(mockMvc)
          .grantType("password")
          .accessTokenUrl("/oauth/token")
          .username(user.getEmail())
          .password(PASSWORD)
          .clientId("d4486b29-7f28-43db-8d4e-44df6b5785c9")
          .clientSecret("a6f59937-fc55-485c-bf91-c8bcdaae2e45")
          .scope("web")
          .getAccessToken();
    // @formatter:on

    return jwt;
  }

  /**
   * Returns user with only USER_ROLE (user@oauth2.com).
   *
   * @return user with email user@oauth2.com and USER_ROLE
   *
   * @throws NoSuchElementException
   */
  public User getBasicUser() {
    final Optional<User> user = userRepository.findByEmail("user@oauth2.com");

    return user.get();
  }
}
