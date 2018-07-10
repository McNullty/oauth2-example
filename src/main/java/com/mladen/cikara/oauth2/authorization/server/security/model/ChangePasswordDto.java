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

  public ChangePasswordDto(
      @Size(max = 50) @NotBlank @JsonProperty("oldPassword") String oldPassword,
      @Size(max = 50) @NotBlank @JsonProperty("newPassword") String newPassword,
      @Size(
          max = 50) @NotBlank @JsonProperty("newPasswordConfirmation") String newPasswordConfirmation) {
    super();
    this.oldPassword = oldPassword;
    this.newPassword = newPassword;
    this.newPasswordConfirmation = newPasswordConfirmation;
  }

  public String getNewPassword() {
    return newPassword;
  }

  public String getNewPasswordConfirmation() {
    return newPasswordConfirmation;
  }

  public String getOldPassword() {
    return oldPassword;
  }

  @Override
  public String toString() {
    return "ChangePasswordDto [oldPassword=" + oldPassword + ", newPassword=" + newPassword
        + ", newPasswordConfirmation=" + newPasswordConfirmation + "]";
  }

}
