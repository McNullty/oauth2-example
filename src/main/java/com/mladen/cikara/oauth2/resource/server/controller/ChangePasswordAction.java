package com.mladen.cikara.oauth2.resource.server.controller;

import com.mladen.cikara.oauth2.authorization.server.security.model.ChangePasswordDto;
import com.mladen.cikara.oauth2.authorization.server.security.model.SpringSecurityUserAdapter;
import com.mladen.cikara.oauth2.authorization.server.security.service.UserService;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChangePasswordAction {
  private static final Logger logger = LoggerFactory.getLogger(ChangePasswordAction.class);

  private final UserService userService;

  public ChangePasswordAction(final UserService userService) {
    this.userService = userService;
  }

  /**
   * End point for changing password.
   *
   * @param changePasswordDto
   *          Object created from json object
   * @param currentUserAdaptor
   *          Current User extracted from authentication
   * @return
   */
  @PostMapping("/change-password")
  public ResponseEntity<?> changePassword(
      @Valid
      @RequestBody
      final ChangePasswordDto changePasswordDto,
      @AuthenticationPrincipal
      final SpringSecurityUserAdapter currentUserAdaptor) {
    logger.trace("Changing password");

    this.userService.changePassword(currentUserAdaptor.getUser().getId(), changePasswordDto);

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
