package com.mladen.cikara.oauth2.resource.server.controller;

import com.mladen.cikara.oauth2.authorization.server.security.model.SpringSecurityUserAdapter;
import com.mladen.cikara.oauth2.authorization.server.security.model.UserResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/user")
public class UserController {

  private static final Logger logger = LoggerFactory.getLogger(UserController.class);

  @GetMapping(path = "current")
  public ResponseEntity<UserResource> currentUser(
      @AuthenticationPrincipal SpringSecurityUserAdapter currentUser) {
    logger.debug("Authentication principal: {}", currentUser);

    final UserResource userResource = new UserResource(currentUser.getUser());

    return ResponseEntity.ok(userResource);
  }
}
