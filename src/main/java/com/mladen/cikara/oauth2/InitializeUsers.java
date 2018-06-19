package com.mladen.cikara.oauth2;

import com.mladen.cikara.oauth2.authorization.server.security.model.Authority;
import com.mladen.cikara.oauth2.authorization.server.security.model.User;
import com.mladen.cikara.oauth2.authorization.server.security.repository.UserRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.annotation.Priority;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Priority(value = 0)
@Component
public class InitializeUsers implements ApplicationRunner {

  private static final String PASSWORD = "secret";
  private static final String ADMIN_EMAIL = "admin@oauth2.com";
  private static final String USER_EMAIL = "user@oauth2.com";
  private static final String SYS_ADMIN_EMAIL = "sysadmin@oauth2.com";

  private static final Logger logger = LoggerFactory.getLogger(InitializeUsers.class);

  @Autowired
  private UserRepository userRepository;

  private void insertUser(String email, String password, String firstName, String lastName,
      Collection<Authority> authorities) {

    final User user = new User.Builder()
        .email(email)
        .password(password)
        .firstName(firstName)
        .lastName(lastName)
        .authorities(authorities.toArray(new Authority[0])).build();

    userRepository.save(user);

    logger.debug("Inserted user : {}", user);
  }

  @Override
  public void run(ApplicationArguments args) throws Exception {

    if (!userRepository.findByEmail(ADMIN_EMAIL).isPresent()) {

      final Collection<Authority> authorities = new ArrayList<>();
      authorities.addAll(Arrays.asList(Authority.ROLE_ADMIN, Authority.ROLE_USER));

      insertUser(ADMIN_EMAIL, PASSWORD, "Application", "Admin", authorities);
    }

    if (!userRepository.findByEmail(USER_EMAIL).isPresent()) {
      final Collection<Authority> authorities = new ArrayList<>();
      authorities.add(Authority.ROLE_USER);

      insertUser(USER_EMAIL, PASSWORD, "User", "User", authorities);
    }

    if (!userRepository.findByEmail(SYS_ADMIN_EMAIL).isPresent()) {
      final Collection<Authority> authorities = new ArrayList<>();
      authorities.add(Authority.ROLE_SYS_ADMIN);

      insertUser(SYS_ADMIN_EMAIL, PASSWORD, "System", "Admin", authorities);
    }
  }
}
