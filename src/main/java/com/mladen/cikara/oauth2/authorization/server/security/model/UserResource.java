package com.mladen.cikara.oauth2.authorization.server.security.model;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import com.mladen.cikara.oauth2.resource.server.controller.UserController;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;

/**
 * This is object that will be returned by REST API.
 *
 * @author mladen
 *
 */
public class UserResource extends ResourceSupport {

  private final String email;

  private final UUID uuid;

  private final String firstName;
  private final String lastName;
  private final Set<Authority> authorities;

  /**
   * Resource object that will be returned by REST api. It uses HATEOS specification. It adds links
   * to object
   *
   * @param user
   *          User object that is being wrapped.
   */
  public UserResource(final User user) {
    this.email = user.getEmail();
    this.uuid = user.getUuid();
    this.firstName = user.getFirstName();
    this.lastName = user.getLastName();
    this.authorities = user.getAuthorities();

    final Link selfLink =
        linkTo(methodOn(UserController.class).getUser(this.uuid.toString(),
            new SpringSecurityUserAdapter(user)))
                .withSelfRel().withDeprecation("Direct access to user entity");

    add(selfLink);

    final Link addUserAuthoritiesLink = linkTo(methodOn(UserController.class)
        .addUserAuthorities(this.uuid.toString(), new AuthorityDto(new HashSet<>()),
            new SpringSecurityUserAdapter(user)))
                .withRel("addUserAuthority")
                .withDeprecation("Add user authorities. You need admin role to access.");

    add(addUserAuthoritiesLink);

    final Link removeUserAuthoritiesLink = linkTo(methodOn(UserController.class)
        .removeUserAuthorities(this.uuid.toString(), new AuthorityDto(new HashSet<>()),
            new SpringSecurityUserAdapter(user)))
                .withRel("removeUserAuthorities")
                .withDeprecation("Remove user authorities. You need admin role to access.");

    add(removeUserAuthoritiesLink);

    final Link getUserAuthorityLink = linkTo(methodOn(UserController.class)
        .getUserAuthority(this.uuid.toString(), new SpringSecurityUserAdapter(user)))
            .withRel("getUserAuthority")
            .withDeprecation("Get list of authorities for user. You need admin role to access.");

    add(getUserAuthorityLink);

    final Link currentUserLink = linkTo(methodOn(UserController.class)
        .currentUser(new SpringSecurityUserAdapter(user)))
            .withRel("currentUser")
            .withDeprecation("Get currently logged in user");

    add(currentUserLink);

    final Link allUsersLink =
        linkTo(methodOn(UserController.class).getUsers(PageRequest.of(0, 1),
            new SpringSecurityUserAdapter(user)))
                .withRel("allUsers").withDeprecation("List of all users");

    add(allUsersLink);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final UserResource other = (UserResource) obj;
    if (this.uuid == null) {
      if (other.uuid != null) {
        return false;
      }
    } else if (!this.uuid.equals(other.uuid)) {
      return false;
    }
    return true;
  }

  public Set<Authority> getAuthorities() {
    return this.authorities;
  }

  public String getEmail() {
    return this.email;
  }

  public String getFirstName() {
    return this.firstName;
  }

  public String getLastName() {
    return this.lastName;
  }

  public UUID getUuid() {
    return this.uuid;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (this.uuid == null ? 0 : this.uuid.hashCode());
    return result;
  }

}
