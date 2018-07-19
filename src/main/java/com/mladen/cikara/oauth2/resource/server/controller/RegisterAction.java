package com.mladen.cikara.oauth2.resource.server.controller;

import com.mladen.cikara.oauth2.authorization.server.security.model.RegisterUserDto;
import com.mladen.cikara.oauth2.authorization.server.security.model.User;
import com.mladen.cikara.oauth2.authorization.server.security.model.UserResource;
import com.mladen.cikara.oauth2.authorization.server.security.service.UserService;

import java.net.URI;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegisterAction {

  private static final Logger logger = LoggerFactory.getLogger(RegisterAction.class);

  private final UserService userService;

  public RegisterAction(final UserService userService) {
    this.userService = userService;
  }

  /**
   * End point for registering new user.
   *
   * @param userDto
   *          UserDto created from json object
   * @return
   */
  @PostMapping("/register")
  public ResponseEntity<?> registerUser(
      @Valid
      @RequestBody
      final RegisterUserDto userDto) {

    logger.trace("Got user DTO: {}", userDto);

    final User newUser = this.userService.registerUser(userDto);

    logger.trace("Registered user: {}", newUser);

    final UserResource userResource = new UserResource(newUser);

    // FIXME: Location is not returned correctly
    final URI location = URI.create(userResource.getLink("self").getHref());

    return ResponseEntity.created(location).build();
  }
}
