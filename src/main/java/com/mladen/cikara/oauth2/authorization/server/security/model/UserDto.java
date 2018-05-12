package com.mladen.cikara.oauth2.authorization.server.security.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude= {"password", "passwordConfirmation"})
public class UserDto {

	private String email;
	private String firstName;
	private String lastName;
	private String password;
	private String passwordConfirmation;
	
}
