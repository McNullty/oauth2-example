package com.mladen.cikara.oauth2.authorization.server.security.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class UserTest {

  @Test
  public void whenCreatingNewUserWithBuilder_thenNewUserObjectIsCreatedWithUuid() {
    final User user = new User.Builder()
        .email("test@test.com")
        .password("secret")
        .firstName("Test")
        .lastName("Test")
        .authorities(Authority.ROLE_USER).build();

    assertThat(user.getUuid()).isNotNull();
    assertThat(user.getAuthorities()).isNotEmpty();
  }

}
