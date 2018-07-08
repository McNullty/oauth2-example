package com.mladen.cikara.oauth2.authorization.server.security.service;

import com.mladen.cikara.oauth2.authorization.server.security.model.Authority;
import com.mladen.cikara.oauth2.authorization.server.security.model.QUser;
import com.mladen.cikara.oauth2.authorization.server.security.model.RegisterUserDto;
import com.mladen.cikara.oauth2.authorization.server.security.model.UpdateUserDto;
import com.mladen.cikara.oauth2.authorization.server.security.model.User;
import com.mladen.cikara.oauth2.authorization.server.security.repository.UserRepository;
import com.mladen.cikara.oauth2.resource.server.controller.EmailAlreadyRegisterdException;
import com.mladen.cikara.oauth2.resource.server.controller.PasswordsDontMatchException;
import com.querydsl.jpa.impl.JPAUpdateClause;

import java.util.UUID;

import javax.persistence.EntityManager;
import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

  private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

  private final ModelMapper modelMapper;

  private final UserRepository userRepository;

  private final EntityManager entityManager;

  public UserServiceImpl(ModelMapper modelMapper, UserRepository userRepository,
      EntityManager entityManager) {
    this.modelMapper = modelMapper;
    this.userRepository = userRepository;
    this.entityManager = entityManager;
  }

  private User convertToEntity(RegisterUserDto userDto) {
    return modelMapper.map(userDto, User.Builder.class).build();
  }

  @Override
  public Page<User> findAllUsers(Pageable page) {
    logger.trace("Got pagable: {}", page);

    return userRepository.findAll(page);
  }

  @Override
  public User registerUser(@Valid RegisterUserDto userDto) {

    logger.trace("Got user DTO: {}", userDto);

    verifyInputData(userDto);

    final User user = convertToEntity(userDto);

    user.addAuthority(Authority.ROLE_USER);

    logger.trace("Got user: {}", user);

    return userRepository.save(user);
  }

  @Transactional
  @Override
  public User updateUser(String uuid, @Valid UpdateUserDto userDto) {
    final QUser user = QUser.user;

    try {

      new JPAUpdateClause(entityManager, user)
          .where(user.uuid.eq(UUID.fromString(uuid)))
          .set(user.firstName, userDto.getFirstName())
          .set(user.lastName, userDto.getLastName())
          .execute();
    } catch (final Exception e) {
      logger.error(e.getMessage());
    }

    return userRepository.findByUuid(UUID.fromString(uuid)).get();
  }

  private void validateEmailDoesntExist(String email) {
    if (userRepository.findByEmail(email).isPresent()) {
      throw new EmailAlreadyRegisterdException();
    }
  }

  private void validatePasswords(RegisterUserDto userDto) {
    if (!userDto.getPassword().equals(userDto.getPasswordConfirmation())) {
      throw new PasswordsDontMatchException();
    }
  }

  private void verifyInputData(RegisterUserDto userDto) {
    validatePasswords(userDto);

    validateEmailDoesntExist(userDto.getEmail());
  }
}
