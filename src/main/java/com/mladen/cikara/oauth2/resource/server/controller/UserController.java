package com.mladen.cikara.oauth2.resource.server.controller;

import com.mladen.cikara.oauth2.authorization.server.security.model.Authority;
import com.mladen.cikara.oauth2.authorization.server.security.model.AuthorityDto;
import com.mladen.cikara.oauth2.authorization.server.security.model.SpringSecurityUserAdapter;
import com.mladen.cikara.oauth2.authorization.server.security.model.UpdateUserDto;
import com.mladen.cikara.oauth2.authorization.server.security.model.User;
import com.mladen.cikara.oauth2.authorization.server.security.model.UserResource;
import com.mladen.cikara.oauth2.authorization.server.security.repository.UserRepository;
import com.mladen.cikara.oauth2.authorization.server.security.service.EntityNotFoundException;
import com.mladen.cikara.oauth2.authorization.server.security.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

  @PostMapping("/{uuid}/add-authority")
  public ResponseEntity<AuthorityDto> addUserAuthorities(@PathVariable String uuid,
      @Valid @RequestBody AuthorityDto authorityDto,
      @AuthenticationPrincipal SpringSecurityUserAdapter currentUserAdaptor) {
    logger.trace("AuthortyDto: {}", authorityDto);

    if (checkUserHasAdminRole(currentUserAdaptor.getUser())) {
      final AuthorityDto authorityDtoResponse = userService.addUserAuthorities(uuid, authorityDto);
      return ResponseEntity.ok(authorityDtoResponse);
    }

    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
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

  private Page<UserResource> convertUserPageToUserResourcePage(final Page<User> userPage) {
    final List<User> users = userPage.getContent();
    final List<UserResource> userResources =
        users.stream().map(user -> new UserResource(user)).collect(Collectors.toList());

    logger.trace("Converted to UserResource {}:", userResources);

    final Page<UserResource> userResourcePage =
        new PageImpl<>(userResources, userPage.getPageable(), userPage.getTotalElements());
    return userResourcePage;
  }

  private Page<UserResource> createPageWithOnlyCurrentUser(
      SpringSecurityUserAdapter currentUserAdaptor) {
    final List<UserResource> content = new ArrayList<>();
    content.add(new UserResource(currentUserAdaptor.getUser()));

    final Page<UserResource> userResourcePage = new PageImpl<>(content);
    return userResourcePage;
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

  @DeleteMapping(path = "/{uuid}")
  public ResponseEntity<Object> deleteUser(@PathVariable String uuid,
      @AuthenticationPrincipal SpringSecurityUserAdapter currentUserAdaptor)
      throws EntityNotFoundException {
    if (checkUserHasAdminRole(currentUserAdaptor.getUser())) {

      logger.debug("deleting user: {}", uuid);

      userService.deleteUser(uuid);

      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
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

  @GetMapping(path = "/{uuid}/authority")
  public ResponseEntity<AuthorityDto> getUserAuthority(@PathVariable String uuid,
      @AuthenticationPrincipal SpringSecurityUserAdapter currentUserAdaptor) {
    final Optional<User> user = userRepository.findByUuid(UUID.fromString(uuid));

    if (checkUserHasAdminRole(currentUserAdaptor.getUser())) {
      if (user.isPresent()) {
        return ResponseEntity
            .ok(new AuthorityDto(user.get().getAuthorities()));
      }
    }

    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

  }

  @GetMapping
  public ResponseEntity<Page<UserResource>> getUsers(Pageable page,
      @AuthenticationPrincipal SpringSecurityUserAdapter currentUserAdaptor) {

    logger.debug("Authentication principal: {}", currentUserAdaptor);

    if (checkUserHasAdminRole(currentUserAdaptor.getUser())) {

      final Page<User> userPage = userService.findAllUsers(page);

      logger.trace("Got page {}:", userPage);

      final Page<UserResource> userResourcePage = convertUserPageToUserResourcePage(userPage);

      logger.trace("Converted to UserResourcePage {}:", userResourcePage);

      return ResponseEntity.ok(userResourcePage);
    } else {
      final Page<UserResource> userResourcePage = createPageWithOnlyCurrentUser(currentUserAdaptor);

      logger.trace("Created new page with only one user {}:", userResourcePage);

      return ResponseEntity.ok(userResourcePage);
    }
  }

  @PostMapping("/{uuid}/remove-authority")
  public ResponseEntity<AuthorityDto> removeUserAuthorities(@PathVariable String uuid,
      @Valid @RequestBody AuthorityDto authorityDto,
      @AuthenticationPrincipal SpringSecurityUserAdapter currentUserAdaptor) {
    logger.trace("AuthortyDto: {}", authorityDto);

    if (checkUserHasAdminRole(currentUserAdaptor.getUser())) {
      final AuthorityDto authorityDtoResponse =
          userService.removeUserAuthorities(uuid, authorityDto);
      return ResponseEntity.ok(authorityDtoResponse);
    }

    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
  }

  private ResponseEntity<UserResource> updateUser(String uuid, @Valid UpdateUserDto userDto)
      throws EntityNotFoundException {
    final User updatedUser = userService.updateUser(uuid, userDto);

    final UserResource updatedUserResource = new UserResource(updatedUser);

    return ResponseEntity.ok(updatedUserResource);
  }

  @PutMapping(path = "/{uuid}")
  public ResponseEntity<UserResource> updateUser(@PathVariable String uuid,
      @Valid @RequestBody UpdateUserDto userDto,
      @AuthenticationPrincipal SpringSecurityUserAdapter currentUserAdaptor)
      throws EntityNotFoundException {
    logger.debug("Authentication principal: {}", currentUserAdaptor);
    logger.debug("UpdateUserDto: {}", userDto);

    if (checkIsUUIDFromCurrentUser(uuid, currentUserAdaptor.getUser())) {
      return updateUser(uuid, userDto);
    }

    if (checkUserHasAdminRole(currentUserAdaptor.getUser())) {
      return updateUser(uuid, userDto);
    }

    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
  }
}
