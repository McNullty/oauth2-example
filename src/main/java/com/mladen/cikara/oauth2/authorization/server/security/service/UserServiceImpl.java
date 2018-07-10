package com.mladen.cikara.oauth2.authorization.server.security.service;

import com.mladen.cikara.oauth2.authorization.server.security.model.Authority;
import com.mladen.cikara.oauth2.authorization.server.security.model.AuthorityDto;
import com.mladen.cikara.oauth2.authorization.server.security.model.ChangePasswordDto;
import com.mladen.cikara.oauth2.authorization.server.security.model.QUser;
import com.mladen.cikara.oauth2.authorization.server.security.model.RegisterUserDto;
import com.mladen.cikara.oauth2.authorization.server.security.model.UpdateUserDto;
import com.mladen.cikara.oauth2.authorization.server.security.model.User;
import com.mladen.cikara.oauth2.authorization.server.security.repository.UserRepository;
import com.mladen.cikara.oauth2.resource.server.controller.EmailAlreadyRegisterdException;
import com.mladen.cikara.oauth2.resource.server.controller.PasswordsDontMatchException;
import com.querydsl.jpa.impl.JPADeleteClause;
import com.querydsl.jpa.impl.JPAUpdateClause;

import java.util.Optional;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
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

  @Override
  public AuthorityDto addUserAuthorities(String uuid, @Valid AuthorityDto authorityDto) {
    final Optional<User> optionalUser = userRepository.findByUuid(UUID.fromString(uuid));

    final User user = optionalUser.get();
    user.addAllAuthority(authorityDto.getAuthorities().toArray(new Authority[0]));

    final User updatedUser = userRepository.save(user);

    return new AuthorityDto(updatedUser.getAuthorities());
  }

  @Transactional
  @Override
  public void changePassword(Long id, @Valid ChangePasswordDto changePasswordDto) {

    final Optional<User> optionalUser = userRepository.findById(id);
    final User user = optionalUser.get();

    final PasswordEncoder passwordEncoder =
        PasswordEncoderFactories.createDelegatingPasswordEncoder();

    if (passwordEncoder.matches(changePasswordDto.getOldPassword(), user.getPassword())) {
      if (changePasswordDto.getNewPassword()
          .equals(changePasswordDto.getNewPasswordConfirmation())) {

        final QUser quser = QUser.user;

        final long numberOfAffectedRows = new JPAUpdateClause(entityManager, quser)
            .where(quser.id.eq(id))
            .set(quser.password, passwordEncoder.encode(changePasswordDto.getNewPassword()))
            .execute();

        if (numberOfAffectedRows == 0) {
          throw new EntityNotFoundException("Password not changed because user was not found.");
        }

      } else {
        throw new PasswordsDontMatchException(
            "New password and adn password confimation do not match");
      }
    } else {
      throw new WrongPasswordsException("Wrong password.");
    }
  }

  private User convertToEntity(RegisterUserDto userDto) {
    return modelMapper.map(userDto, User.Builder.class).build();
  }

  @Transactional
  @Override
  public void deleteUser(String uuid) throws EntityNotFoundException {
    final QUser user = QUser.user;

    final long numberOfAffectedRows = new JPADeleteClause(entityManager, user)
        .where(user.uuid.eq(UUID.fromString(uuid))).execute();

    if (numberOfAffectedRows == 0) {
      throw new EntityNotFoundException("Couldn't delete user, uuid not found");
    }
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

  @Override
  public AuthorityDto removeUserAuthorities(String uuid, @Valid AuthorityDto authorityDto) {
    final Optional<User> optionalUser = userRepository.findByUuid(UUID.fromString(uuid));

    final User user = optionalUser.get();
    user.removeAllAuthority(authorityDto.getAuthorities().toArray(new Authority[0]));

    final User updatedUser = userRepository.save(user);

    return new AuthorityDto(updatedUser.getAuthorities());
  }

  @Transactional
  @Override
  public User updateUser(String uuid, @Valid UpdateUserDto userDto) throws EntityNotFoundException {
    final QUser user = QUser.user;

    final long numberOfAffectedRows = new JPAUpdateClause(entityManager, user)
        .where(user.uuid.eq(UUID.fromString(uuid)))
        .set(user.firstName, userDto.getFirstName())
        .set(user.lastName, userDto.getLastName())
        .execute();

    if (numberOfAffectedRows == 0) {
      throw new EntityNotFoundException("Couldn't update user, uuid not found");
    }

    return userRepository.findByUuid(UUID.fromString(uuid)).get();
  }

  private void validateEmailDoesntExist(String email) {
    if (userRepository.findByEmail(email).isPresent()) {
      throw new EmailAlreadyRegisterdException("Email already registered");
    }
  }

  private void validatePasswords(RegisterUserDto userDto) {
    if (!userDto.getPassword().equals(userDto.getPasswordConfirmation())) {
      throw new PasswordsDontMatchException("Passwords do not match");
    }
  }

  private void verifyInputData(RegisterUserDto userDto) {
    validatePasswords(userDto);

    validateEmailDoesntExist(userDto.getEmail());
  }
}
