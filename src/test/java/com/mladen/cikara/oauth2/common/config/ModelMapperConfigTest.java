package com.mladen.cikara.oauth2.common.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.mladen.cikara.oauth2.authorization.server.security.model.User;
import com.mladen.cikara.oauth2.authorization.server.security.model.UserDto;

import org.junit.Test;

public class ModelMapperConfigTest {

  @Test
  public void testMappingValidUserDto() {
    final ModelMapperConfig modelMapperConfig = new ModelMapperConfig();

    final UserDto userDto = new UserDto();
    userDto.setEmail("test@test.com");
    userDto.setFirstName("Test");
    userDto.setLastName("Testowski");
    userDto.setPassword("secret");
    userDto.setPasswordConfirmation("secret");

    final User user = modelMapperConfig.modelMapper().map(userDto, User.class);

    assertThat(user)
        .extracting("email", "firstName", "lastName")
        .contains("test@test.com", "Test", "Testowski");

    assertThat(user.getPassword()).startsWith("{bcrypt}");
  }

}
