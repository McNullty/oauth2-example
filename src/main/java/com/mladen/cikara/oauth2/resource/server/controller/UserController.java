package com.mladen.cikara.oauth2.resource.server.controller;

import com.mladen.cikara.oauth2.authorization.server.security.model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

  private static final Logger logger = LoggerFactory.getLogger(UserController.class);

  @GetMapping(path = "/me")
  public ResponseEntity<User> currentUser(
      @AuthenticationPrincipal org.springframework.security.core.userdetails.User currentUser,
      @RequestHeader(value = "Authorization") String authorization) {
    logger.debug("Authentication principal: {}", currentUser);
    logger.debug("Authorization: {}", authorization);

    // final Authentication authentication =
    // SecurityContextHolder.getContext().getAuthentication();
    //
    // logger.debug("Authentication: {}", authentication);
    //
    // final org.springframework.security.core.userdetails.User user =
    // (org.springframework.security.core.userdetails.User)
    // authentication.getPrincipal();
    //
    // logger.debug("User: {}", user);

    return null;
  }
}
