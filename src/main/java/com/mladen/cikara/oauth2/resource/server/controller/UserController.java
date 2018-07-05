package com.mladen.cikara.oauth2.resource.server.controller;

import com.mladen.cikara.oauth2.authorization.server.security.model.Authority;
import com.mladen.cikara.oauth2.authorization.server.security.model.SpringSecurityUserAdapter;
import com.mladen.cikara.oauth2.authorization.server.security.model.User;
import com.mladen.cikara.oauth2.authorization.server.security.model.UserResource;
import com.mladen.cikara.oauth2.authorization.server.security.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/user")
public class UserController {

  private static final Logger logger = LoggerFactory.getLogger(UserController.class);

  private final UserRepository userRepository;

  public UserController(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /**
   * Checks if currently logged in user has UUID same as requested
   *
   * @param uuid
   * @param currentUser
   * @return
   */
  private boolean checkIsUUIDFromCurrentUser(String uuid, User currentUser) {

    final String uuidForCurrentUser = currentUser.getUUID().toString();

    return uuidForCurrentUser.equals(uuid);
  }

  /**
   * Checks if current user has ADMIN_ROLE
   *
   * @param user
   * @return
   */
  private boolean checkUserHasAdminRole(User user) {
    return user.getAuthorities().contains(Authority.ROLE_ADMIN);
  }

  private ResponseEntity<UserResource> createResponseEntityFromUser(User user) {
    final UserResource userResource = new UserResource(user);

    return ResponseEntity.ok(userResource);
  }

  @GetMapping(path = "/current")
  public ResponseEntity<UserResource> currentUser(
      @AuthenticationPrincipal SpringSecurityUserAdapter currentUserAdaptor) {
    logger.debug("Authentication principal: {}", currentUserAdaptor);

    return createResponseEntityFromUser(currentUserAdaptor.getUser());
  }

  @GetMapping(path = "/{uuid}")
  public ResponseEntity<UserResource> getUser(@PathVariable String uuid,
      @AuthenticationPrincipal SpringSecurityUserAdapter currentUserAdaptor) {

    logger.debug("Authentication principal: {}", currentUserAdaptor);

    final User currentUser = currentUserAdaptor.getUser();

    if (checkIsUUIDFromCurrentUser(uuid, currentUser)) {
      return createResponseEntityFromUser(currentUser);
    }

    if (checkUserHasAdminRole(currentUser)) {
      final Optional<User> user = userRepository.findByUuid(UUID.fromString(uuid));

      if (user.isPresent()) {
        return createResponseEntityFromUser(user.get());
      } else {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      }
    }

    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
  }
}
