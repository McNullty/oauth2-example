package com.mladen.cikara.oauth2.authorization.server.security.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude= {"password", "passwordConfirmation"})
public class UserDto {

	@Email
	@NotNull
	private String email;
	
	@Size(max = 50)
	@NotBlank
	private String firstName;
	
	@Size(max = 50)
	@NotBlank
	private String lastName;

	@Size(max = 50)
	@NotBlank
	private String password;
	
	@Size(max = 50)
	@NotBlank
	private String passwordConfirmation;
	
}
