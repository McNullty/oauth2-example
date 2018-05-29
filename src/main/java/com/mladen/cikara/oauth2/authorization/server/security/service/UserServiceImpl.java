package com.mladen.cikara.oauth2.authorization.server.security.service;

import com.mladen.cikara.oauth2.authorization.server.security.model.Authority;
import com.mladen.cikara.oauth2.authorization.server.security.model.User;
import com.mladen.cikara.oauth2.authorization.server.security.model.UserDto;
import com.mladen.cikara.oauth2.authorization.server.security.repository.UserRepository;
import com.mladen.cikara.oauth2.resource.server.controller.EmailAlreadyRegisterd;
import com.mladen.cikara.oauth2.resource.server.controller.PasswordsDontMatchException;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

  private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

  private final ModelMapper modelMapper;

  private final UserRepository userRepository;

  public UserServiceImpl(ModelMapper modelMapper, UserRepository userRepository) {
    this.modelMapper = modelMapper;
    this.userRepository = userRepository;
  }

  private User convertToEntity(UserDto userDto) {
    return modelMapper.map(userDto, User.class);
  }

  @Override
  public User registerUser(@Valid UserDto userDto) {

    logger.trace("Got user DTO: {}", userDto);

    verifyInputData(userDto);

    final User user = convertToEntity(userDto);

    user.addAuthority(Authority.ROLE_USER);

    logger.trace("Got user: {}", user);

    return userRepository.save(user);
  }

  private void validateEmailDoesntExist(String email) {
    if (userRepository.findByEmail(email).isPresent()) {
      throw new EmailAlreadyRegisterd();
    }
  }

  private void validatePasswords(UserDto userDto) {
    if (!userDto.getPassword().equals(userDto.getPasswordConfirmation())) {
      throw new PasswordsDontMatchException();
    }
  }

  private void verifyInputData(UserDto userDto) {
    validatePasswords(userDto);

    validateEmailDoesntExist(userDto.getEmail());
  }
}
