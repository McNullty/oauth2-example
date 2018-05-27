package com.mladen.cikara.oauth2.authorization.server.security.service;

import javax.validation.Valid;

import com.mladen.cikara.oauth2.authorization.server.security.model.User;
import com.mladen.cikara.oauth2.authorization.server.security.model.UserDto;

public interface UserService {
	public User registerUser(@Valid UserDto userDto);
}
