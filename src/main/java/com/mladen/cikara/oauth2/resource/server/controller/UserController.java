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

  public UserController(final UserRepository userRepository, final UserService userService) {
    this.userRepository = userRepository;
    this.userService = userService;
  }

  /**
   * End point for adding one ore more Authorities to user.
   *
   * @param uuid
   *          UUID of user that is to be changed
   * @param authorityDto
   *          List of authorities to be added to user
   * @param currentUserAdaptor
   *          Logged in user form authorization
   * @return
   */
  @PostMapping("/{uuid}/add-authority")
  public ResponseEntity<AuthorityDto> addUserAuthorities(
      @PathVariable
      final String uuid,
      @Valid
      @RequestBody
      final AuthorityDto authorityDto,
      @AuthenticationPrincipal
      final SpringSecurityUserAdapter currentUserAdaptor) {
    logger.trace("AuthortyDto: {}", authorityDto);

    if (checkUserHasAdminRole(currentUserAdaptor.getUser())) {
      final AuthorityDto authorityDtoResponse =
          this.userService.addUserAuthorities(uuid, authorityDto);
      return ResponseEntity.ok(authorityDtoResponse);
    }

    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
  }

  /**
   * Checks if currently logged in user has UUID same as requested.
   *
   * @param uuid
   *          Uuid of user that will be changed
   * @param currentUser
   *          Logged in user
   * @return
   */
  private boolean checkIsUuidFromCurrentUser(final String uuid, final User currentUser) {

    final String uuidForCurrentUser = currentUser.getUuid().toString();

    return uuidForCurrentUser.equals(uuid);
  }

  /**
   * Checks if current user has ADMIN_ROLE.
   *
   * @param user
   *          Logged in user
   * @return
   */
  private boolean checkUserHasAdminRole(final User user) {
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
      final SpringSecurityUserAdapter currentUserAdaptor) {
    final List<UserResource> content = new ArrayList<>();
    content.add(new UserResource(currentUserAdaptor.getUser()));

    final Page<UserResource> userResourcePage = new PageImpl<>(content);
    return userResourcePage;
  }

  private ResponseEntity<UserResource> createResponseEntityFromUser(final User user) {
    final UserResource userResource = new UserResource(user);

    return ResponseEntity.ok(userResource);
  }

  /**
   * End point for getting currently logged in user data.
   *
   * @param currentUserAdaptor
   *          Logged in user
   * @return
   */
  @GetMapping(path = "/current")
  public ResponseEntity<UserResource> currentUser(
      @AuthenticationPrincipal
      final SpringSecurityUserAdapter currentUserAdaptor) {
    logger.debug("Authentication principal: {}", currentUserAdaptor);

    return createResponseEntityFromUser(currentUserAdaptor.getUser());
  }

  /**
   * End point for deleting user with given uuid.
   *
   * @param uuid
   *          UUID of user we want to delete
   * @param currentUserAdaptor
   *          Logged in user
   * @return
   */
  @DeleteMapping(path = "/{uuid}")
  public ResponseEntity<Object> deleteUser(
      @PathVariable
      final String uuid,
      @AuthenticationPrincipal
      final SpringSecurityUserAdapter currentUserAdaptor) {
    if (checkUserHasAdminRole(currentUserAdaptor.getUser())) {

      logger.debug("deleting user: {}", uuid);

      this.userService.deleteUser(uuid);

      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
  }

  /**
   * End point that returns user data for given uuid.
   *
   * @param uuid
   *          UUID of user want to check
   * @param currentUserAdaptor
   *          Logged in user
   * @return
   */
  @GetMapping(path = "/{uuid}")
  public ResponseEntity<UserResource> getUser(
      @PathVariable
      final String uuid,
      @AuthenticationPrincipal
      final SpringSecurityUserAdapter currentUserAdaptor) {

    logger.debug("Authentication principal: {}", currentUserAdaptor);

    final User currentUser = currentUserAdaptor.getUser();

    if (checkIsUuidFromCurrentUser(uuid, currentUser)) {
      return createResponseEntityFromUser(currentUser);
    }

    if (checkUserHasAdminRole(currentUser)) {
      final Optional<User> user = this.userRepository.findByUuid(UUID.fromString(uuid));

      if (user.isPresent()) {
        return createResponseEntityFromUser(user.get());
      } else {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      }
    }

    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
  }

  /**
   * End point for listing all authorities for user.
   *
   * @param uuid
   *          UUID of user we want to check authority
   * @param currentUserAdaptor
   *          currently logged in user
   * @return
   */
  @GetMapping(path = "/{uuid}/authority")
  public ResponseEntity<AuthorityDto> getUserAuthority(
      @PathVariable
      final String uuid,
      @AuthenticationPrincipal
      final SpringSecurityUserAdapter currentUserAdaptor) {
    final Optional<User> user = this.userRepository.findByUuid(UUID.fromString(uuid));

    if (checkUserHasAdminRole(currentUserAdaptor.getUser()) && user.isPresent()) {
      return ResponseEntity
          .ok(new AuthorityDto(user.get().getAuthorities()));
    }

    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

  }

  /**
   * List of all users. Pageable.
   *
   * @param page
   *          Page we want to get
   * @param currentUserAdaptor
   *          Currently logged in user
   * @return
   */
  @GetMapping
  public ResponseEntity<Page<UserResource>> getUsers(final Pageable page,
      @AuthenticationPrincipal
      final SpringSecurityUserAdapter currentUserAdaptor) {

    logger.debug("Authentication principal: {}", currentUserAdaptor);

    if (checkUserHasAdminRole(currentUserAdaptor.getUser())) {

      final Page<User> userPage = this.userService.findAllUsers(page);

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

  /**
   * Removes authority from user with given UUID.
   *
   * @param uuid
   *          UUID of user that is to be changed
   * @param authorityDto
   *          List of authorities that will be removed
   * @param currentUserAdaptor
   *          Currently logged in user
   * @return
   */
  @PostMapping("/{uuid}/remove-authority")
  public ResponseEntity<AuthorityDto> removeUserAuthorities(
      @PathVariable
      final String uuid,
      @Valid
      @RequestBody
      final AuthorityDto authorityDto,
      @AuthenticationPrincipal
      final SpringSecurityUserAdapter currentUserAdaptor) {
    logger.trace("AuthortyDto: {}", authorityDto);

    if (checkUserHasAdminRole(currentUserAdaptor.getUser())) {
      final AuthorityDto authorityDtoResponse =
          this.userService.removeUserAuthorities(uuid, authorityDto);
      return ResponseEntity.ok(authorityDtoResponse);
    }

    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
  }

  private ResponseEntity<UserResource> updateUser(final String uuid,
      @Valid
      final UpdateUserDto userDto)
      throws EntityNotFoundException {
    final User updatedUser = this.userService.updateUser(uuid, userDto);

    final UserResource updatedUserResource = new UserResource(updatedUser);

    return ResponseEntity.ok(updatedUserResource);
  }

  /**
   * Update user data.
   *
   * @param uuid
   *          UUID for user that is to be changed
   * @param userDto
   *          DTO with changes to user data
   * @param currentUserAdaptor
   *          Currently logged in user
   * @return
   */
  @PutMapping(path = "/{uuid}")
  public ResponseEntity<UserResource> updateUser(
      @PathVariable
      final String uuid,
      @Valid
      @RequestBody
      final UpdateUserDto userDto,
      @AuthenticationPrincipal
      final SpringSecurityUserAdapter currentUserAdaptor) {
    logger.debug("Authentication principal: {}", currentUserAdaptor);
    logger.debug("UpdateUserDto: {}", userDto);

    if (checkIsUuidFromCurrentUser(uuid, currentUserAdaptor.getUser())) {
      return updateUser(uuid, userDto);
    }

    if (checkUserHasAdminRole(currentUserAdaptor.getUser())) {
      return updateUser(uuid, userDto);
    }

    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
  }
}
