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

  @Override
  public boolean equals(Object obj) {
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
    if (user == null) {
      if (other.user != null) {
        return false;
      }
    } else if (!user.equals(other.user)) {
      return false;
    }
    return true;
  }

  public User getUser() {
    return user;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((user == null) ? 0 : user.hashCode());
    return result;
  }
}
