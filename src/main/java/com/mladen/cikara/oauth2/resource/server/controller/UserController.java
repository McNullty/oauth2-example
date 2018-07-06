package com.mladen.cikara.oauth2.resource.server.controller;

import com.mladen.cikara.oauth2.authorization.server.security.model.Authority;
import com.mladen.cikara.oauth2.authorization.server.security.model.SpringSecurityUserAdapter;
import com.mladen.cikara.oauth2.authorization.server.security.model.User;
import com.mladen.cikara.oauth2.authorization.server.security.model.UserResource;
import com.mladen.cikara.oauth2.authorization.server.security.repository.UserRepository;
import com.mladen.cikara.oauth2.authorization.server.security.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
  private final UserService userService;

  public UserController(UserRepository userRepository, UserService userService) {
    this.userRepository = userRepository;
    this.userService = userService;
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

  @GetMapping
  public ResponseEntity<Page<UserResource>> getUsers(Pageable page,
      @AuthenticationPrincipal SpringSecurityUserAdapter currentUserAdaptor) {

    logger.debug("Authentication principal: {}", currentUserAdaptor);

    final Page<User> userPage = userService.findAllUsers(page);

    logger.trace("Got page {}:", userPage);

    final List<User> users = userPage.getContent();
    final List<UserResource> userResources =
        users.stream().map(user -> new UserResource(user)).collect(Collectors.toList());

    logger.trace("Converted to UserResource {}:", userResources);

    final Page<UserResource> userResourcePage =
        new PageImpl<>(userResources, userPage.getPageable(), userPage.getTotalElements());

    logger.trace("Converted to UserResourcePage {}:", userResourcePage);

    return ResponseEntity.ok(userResourcePage);
  }
}
