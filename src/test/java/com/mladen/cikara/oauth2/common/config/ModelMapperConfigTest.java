package com.mladen.cikara.oauth2.common.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.mladen.cikara.oauth2.authorization.server.security.model.RegisterUserDto;
import com.mladen.cikara.oauth2.authorization.server.security.model.User;

import org.junit.Test;

public class ModelMapperConfigTest {

  @Test
  public void testMappingValidRegisterUserDto() {
    final ModelMapperConfig modelMapperConfig = new ModelMapperConfig();

    final RegisterUserDto registerUserDto = new RegisterUserDto.Builder()
        .email("test@test.com")
        .firstName("Test")
        .lastName("Testowski")
        .password("secret")
        .passwordConfirmation("secret")
        .build();

    final User user =
        modelMapperConfig.modelMapper().map(registerUserDto, User.Builder.class).build();

    assertThat(user)
        .extracting("email", "firstName", "lastName")
        .contains("test@test.com", "Test", "Testowski");

    assertThat(user.getPassword()).startsWith("{bcrypt}");
  }

}
