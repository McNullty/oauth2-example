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

    /**
     * Creates new instance RegisterUserDto object but checks that passwords are correct and equal.
     *
     * @return RegisterUserDto object
     */
    public RegisterUserDto build() {
      if (this.password == null || this.passwordConfirmation == null || this.password.isEmpty()
          || this.passwordConfirmation.isEmpty()
          || !this.password.equals(this.passwordConfirmation)) {

        throw new PasswordsDontMatchException("Passwords not set");
      }

      return new RegisterUserDto(this);
    }

    public Builder email(final String email) {
      this.email = email;
      return this;
    }

    public Builder firstName(final String firstName) {
      this.firstName = firstName;
      return this;
    }

    public Builder lastName(final String lastName) {
      this.lastName = lastName;
      return this;
    }

    /**
     * Set clear password.
     *
     * @param password
     *          Clear text password
     * @return builder
     */
    public Builder password(final String password) {
      this.password = password;
      return this;
    }

    /**
     * Confirmation for clear password.
     *
     * @param password
     *          Clear text password
     * @return builder
     */
    public Builder passwordConfirmation(final String password) {
      this.passwordConfirmation = password;
      return this;
    }
  }

  @Email
  @NotBlank
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

  /**
   * Creates object using bulder.
   *
   * @param builder
   *          Instance of builder that was configured for new object.
   */
  public RegisterUserDto(final Builder builder) {
    this.email = builder.email;
    this.firstName = builder.firstName;
    this.lastName = builder.lastName;
    this.password = builder.password;
    this.passwordConfirmation = builder.passwordConfirmation;
  }

  @JsonCreator
  RegisterUserDto(
      @Email
      @NotNull
      @JsonProperty("email")
      final String email,
      @Size(max = 50)
      @NotBlank
      @JsonProperty("firstName")
      final String firstName,
      @Size(max = 50)
      @NotBlank
      @JsonProperty("lastName")
      final String lastName,
      @Size(max = 50)
      @NotBlank
      @JsonProperty("password")
      final String password,
      @Size(max = 50)
      @NotBlank
      @JsonProperty("passwordConfirmation")
      final String passwordConfirmation) {

    super();
    this.email = email;
    this.firstName = firstName;
    this.lastName = lastName;
    this.password = password;
    this.passwordConfirmation = passwordConfirmation;
  }

  @Override
  public boolean equals(final Object obj) {
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
    if (this.email == null) {
      if (other.email != null) {
        return false;
      }
    } else if (!this.email.equals(other.email)) {
      return false;
    }
    return true;
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

  public String getPassword() {
    return this.password;
  }

  public String getPasswordConfirmation() {
    return this.passwordConfirmation;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (this.email == null ? 0 : this.email.hashCode());
    return result;
  }

  @Override
  public String toString() {
    return "UserDto [email=" + this.email + ", firstName=" + this.firstName + ", lastName="
        + this.lastName + "]";
  }

}
