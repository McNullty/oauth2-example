package com.mladen.cikara.oauth2.authorization.server.security.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.mladen.cikara.oauth2.resource.server.controller.PasswordsDontMatchException;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@JsonRootName("user")
public class RegisterUserDto {

  public static class Builder {
    private String email;
    private String password;
    private String passwordConfirmation;
    private String firstName;
    private String lastName;

    public RegisterUserDto build() {
      if (password == null || passwordConfirmation == null || password.isEmpty()
          || passwordConfirmation.isEmpty() || !password.equals(passwordConfirmation)) {

        throw new PasswordsDontMatchException();
      }

      return new RegisterUserDto(this);
    }

    public Builder email(String email) {
      this.email = email;
      return this;
    }

    public Builder firstName(String firstName) {
      this.firstName = firstName;
      return this;
    }

    public Builder lastName(String lastName) {
      this.lastName = lastName;
      return this;
    }

    /**
     *
     * @param password
     *          Clear text password
     * @return
     */
    public Builder password(String password) {
      this.password = password;
      return this;
    }

    /**
     *
     * @param password
     *          Clear text password
     * @return
     */
    public Builder passwordConfirmation(String password) {
      passwordConfirmation = password;
      return this;
    }
  }

  @Email
  @NotNull
  private final String email;

  @Size(max = 50)
  @NotBlank
  private final String firstName;

  @Size(max = 50)
  @NotBlank
  private final String lastName;

  @Size(max = 50)
  @NotBlank
  private final String password;

  @Size(max = 50)
  @NotBlank
  private final String passwordConfirmation;

  public RegisterUserDto(Builder builder) {
    email = builder.email;
    firstName = builder.firstName;
    lastName = builder.lastName;
    password = builder.password;
    passwordConfirmation = builder.passwordConfirmation;
  }

  @JsonCreator
  RegisterUserDto(@Email @NotNull @JsonProperty("email") String email,
      @Size(max = 50) @NotBlank @JsonProperty("firstName") String firstName,
      @Size(max = 50) @NotBlank @JsonProperty("lastName") String lastName,
      @Size(max = 50) @NotBlank @JsonProperty("password") String password,
      @Size(max = 50) @NotBlank @JsonProperty("passwordConfirmation") String passwordConfirmation) {
    super();
    this.email = email;
    this.firstName = firstName;
    this.lastName = lastName;
    this.password = password;
    this.passwordConfirmation = passwordConfirmation;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final RegisterUserDto other = (RegisterUserDto) obj;
    if (email == null) {
      if (other.email != null) {
        return false;
      }
    } else if (!email.equals(other.email)) {
      return false;
    }
    return true;
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

  public String getPassword() {
    return password;
  }

  public String getPasswordConfirmation() {
    return passwordConfirmation;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((email == null) ? 0 : email.hashCode());
    return result;
  }

  @Override
  public String toString() {
    return "UserDto [email=" + email + ", firstName=" + firstName + ", lastName=" + lastName + "]";
  }

}
