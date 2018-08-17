package com.mladen.cikara.oauth2.authorization.server.security.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.mladen.cikara.oauth2.authorization.server.security.model.Authority;
import com.mladen.cikara.oauth2.authorization.server.security.model.RegisterUserDto;
import com.mladen.cikara.oauth2.authorization.server.security.model.User;
import com.mladen.cikara.oauth2.authorization.server.security.repository.UserRepository;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@ActiveProfiles("int-test")
@AutoConfigureMockMvc
@DirtiesContext
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceImplIntTest {

  @Autowired
  private UserService userService;

  @Autowired
  private UserRepository userRepository;

  @Test
  public void whenSavingUser_thenUserIsAssignedRoleUser() {

    final RegisterUserDto userDto = new RegisterUserDto.Builder()
        .email("test@test.com")
        .firstName("testFirstName")
        .lastName("testLastName")
        .password("password")
        .passwordConfirmation("password")
        .build();

    this.userService.registerUser(userDto);

    final Optional<User> createdUser = this.userRepository.findByEmail("test@test.com");

    assertThat(createdUser.get().getAuthorities()).contains(Authority.ROLE_USER);
  }
}
