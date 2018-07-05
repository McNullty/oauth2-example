package com.mladen.cikara.oauth2.authorization.server.security.service;

import com.mladen.cikara.oauth2.authorization.server.security.model.User;
import com.mladen.cikara.oauth2.authorization.server.security.model.RegisterUserDto;

import javax.validation.Valid;

public interface UserService {
  User registerUser(@Valid RegisterUserDto userDto);
}
