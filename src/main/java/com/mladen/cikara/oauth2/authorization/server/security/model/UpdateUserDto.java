package com.mladen.cikara.oauth2.authorization.server.security.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@JsonRootName("user")
public class UpdateUserDto {
  @Size(max = 50)
  @NotBlank
  private final String firstName;
  @Size(max = 50)
  @NotBlank
  private final String lastName;

  @JsonCreator
  public UpdateUserDto(@Size(max = 50) @NotBlank @JsonProperty("firstName") String firstName,
      @Size(max = 50) @NotBlank @JsonProperty("lastName") String lastName) {
    this.firstName = firstName;
    this.lastName = lastName;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  @Override
  public String toString() {
    return "UpdateUserDto [firstName=" + firstName + ", lastName=" + lastName + "]";
  }
}
