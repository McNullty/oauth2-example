package com.mladen.cikara.oauth2.authorization.server.security.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import com.mladen.cikara.oauth2.authorization.server.security.model.User;
import com.mladen.cikara.oauth2.authorization.server.security.model.RegisterUserDto;
import com.mladen.cikara.oauth2.authorization.server.security.repository.UserRepository;
import com.mladen.cikara.oauth2.common.config.ModelMapperConfig;
import com.mladen.cikara.oauth2.resource.server.controller.EmailAlreadyRegisterdException;
import com.mladen.cikara.oauth2.resource.server.controller.PasswordsDontMatchException;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class UserServiceImplTest {

  private UserRepository userRepositoryMock;
  private UserService userService;

  @Test
  public void createUserSuccessfuly() {

    when(userRepositoryMock.findByEmail(eq("test@test.com"))).thenReturn(Optional.empty());
    doAnswer(returnsFirstArg()).when(userRepositoryMock).save(any(User.class));

    final RegisterUserDto registerUserDto = new RegisterUserDto.Builder()
        .email("test@test.com")
        .firstName("testFirstName")
        .lastName("testLastName")
        .password("password")
        .passwordConfirmation("password")
        .build();

    final User user = userService.registerUser(registerUserDto);

    assertThat(user.getEmail()).isEqualTo("test@test.com");
    assertThat(user.getUUID()).isNotNull();
  }

  @Before
  public void setup() {

    final ModelMapperConfig modelMapperConfig = new ModelMapperConfig();

    userRepositoryMock = Mockito.mock(UserRepository.class);
    userService = new UserServiceImpl(modelMapperConfig.modelMapper(), userRepositoryMock);
  }

  @Test(expected = EmailAlreadyRegisterdException.class)
  public void whenCreatingNewUser_andEmailIsAlreadyRegistered_thenExceptionIsRaised()
      throws Exception {
    when(userRepositoryMock.findByEmail(eq("test@test.com")))
        .thenReturn(Optional.of(new User.Builder().password("password").build()));

    final RegisterUserDto registerUserDto = new RegisterUserDto.Builder()
        .email("test@test.com")
        .firstName("testFirstName")
        .lastName("testLastName")
        .password("password")
        .passwordConfirmation("password")
        .build();

    userService.registerUser(registerUserDto);
  }

  @Test(expected = PasswordsDontMatchException.class)
  public void whenCreatingNewUser_andPasswordsDontMatch_thenExceptionIsRaised() throws Exception {
    final RegisterUserDto registerUserDto = new RegisterUserDto.Builder()
        .email("test@test.com")
        .firstName("testFirstName")
        .lastName("testLastName")
        .password("password1")
        .passwordConfirmation("password2")
        .build();

    userService.registerUser(registerUserDto);
  }
}
