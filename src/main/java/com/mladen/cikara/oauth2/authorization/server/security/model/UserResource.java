package com.mladen.cikara.oauth2.authorization.server.security.model;

import org.springframework.hateoas.ResourceSupport;

public class UserResource extends ResourceSupport {

  private final User user;

  public UserResource(User user) {
    this.user = user;

    // TODO: Add link to list of users
    // TODO: Add link to user
    // this.add(linkTo());
  }

  public User getUser() {
    return user;
  }
}
