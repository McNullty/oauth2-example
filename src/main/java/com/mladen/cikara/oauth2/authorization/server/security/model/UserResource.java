package com.mladen.cikara.oauth2.authorization.server.security.model;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import com.mladen.cikara.oauth2.resource.server.controller.UserController;

import java.util.Set;
import java.util.UUID;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;

/**
 * This is object that will be returned by REST API
 *
 * @author Mladen Čikara mladen.cikara@gmail.com
 *
 */
public class UserResource extends ResourceSupport {

  private final String email;
  private final UUID uuid;
  private final String firstName;
  private final String lastName;
  private final Set<Authority> authorities;

  public UserResource(User user) {
    email = user.getEmail();
    uuid = user.getUUID();
    firstName = user.getFirstName();
    lastName = user.getLastName();
    authorities = user.getAuthorities();

    final Link selfLink =
        linkTo(methodOn(UserController.class).getUser(user.getUUID().toString(), null))
            .withSelfRel().withDeprecation("Direct access to user entity");

    add(selfLink);

    final Link allUsersLink =
        linkTo(methodOn(UserController.class).getUsers(null, null))
            .withRel("allUsers").withDeprecation("List of all users");

    add(allUsersLink);
  }

  public Set<Authority> getAuthorities() {
    return authorities;
  }

  public String getEmail() {
    return email;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public UUID getUuid() {
    return uuid;
  }

}
