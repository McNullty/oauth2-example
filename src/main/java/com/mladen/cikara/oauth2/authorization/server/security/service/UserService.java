package com.mladen.cikara.oauth2.authorization.server.security.service;

import com.mladen.cikara.oauth2.authorization.server.security.model.RegisterUserDto;
import com.mladen.cikara.oauth2.authorization.server.security.model.UpdateUserDto;
import com.mladen.cikara.oauth2.authorization.server.security.model.User;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
  void deleteUser(String uuid) throws EntityNotFoundException;

  Page<User> findAllUsers(Pageable page);

  User registerUser(@Valid RegisterUserDto userDto);

  User updateUser(String uuid, @Valid UpdateUserDto userDto) throws EntityNotFoundException;
}
