package com.mladen.cikara.oauth2.authorization.server.security.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@JsonRootName("changePassword")
public class ChangePasswordDto {

  @Size(max = 50)
  @NotBlank
  private final String oldPassword;
  @Size(max = 50)
  @NotBlank
  private final String newPassword;
  @Size(max = 50)
  @NotBlank
  private final String newPasswordConfirmation;

  /**
   * Creates object from json object.
   *
   * @param oldPassword
   *          old password
   * @param newPassword
   *          new password
   * @param newPasswordConfirmation
   *          password confirmation
   */
  public ChangePasswordDto(
      @Size(max = 50)
      @NotBlank
      @JsonProperty("oldPassword")
      final String oldPassword,
      @Size(max = 50)
      @NotBlank
      @JsonProperty("newPassword")
      final String newPassword,
      @Size(
          max = 50)
      @NotBlank
      @JsonProperty("newPasswordConfirmation")
      final String newPasswordConfirmation) {
    super();
    this.oldPassword = oldPassword;
    this.newPassword = newPassword;
    this.newPasswordConfirmation = newPasswordConfirmation;
  }

  public String getNewPassword() {
    return this.newPassword;
  }

  public String getNewPasswordConfirmation() {
    return this.newPasswordConfirmation;
  }

  public String getOldPassword() {
    return this.oldPassword;
  }

  @Override
  public String toString() {
    return "ChangePasswordDto [oldPassword=" + this.oldPassword
        + ", newPassword=" + this.newPassword
        + ", newPasswordConfirmation=" + this.newPasswordConfirmation + "]";
  }

}
