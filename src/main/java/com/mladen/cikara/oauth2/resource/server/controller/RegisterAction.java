package com.mladen.cikara.oauth2.resource.server.controller;

import java.net.URI;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.parsing.Location;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.mladen.cikara.oauth2.authorization.server.security.model.User;
import com.mladen.cikara.oauth2.authorization.server.security.model.UserDto;
import com.mladen.cikara.oauth2.authorization.server.security.model.UserResource;
import com.mladen.cikara.oauth2.authorization.server.security.repository.UserRepository;

@RestController
public class RegisterAction {

	private static final Logger logger = LoggerFactory.getLogger(RegisterAction.class);

	private ModelMapper modelMapper;

	private UserRepository userRepository;

	public RegisterAction(ModelMapper modelMapper, UserRepository userRepository) {
		this.modelMapper = modelMapper;
		this.userRepository = userRepository;
	}

	@PostMapping("/register")
	public ResponseEntity<?> registerUser(@Valid @RequestBody UserDto userDto) {
		logger.trace("Got user DTO: {}", userDto);

		verifyInputData(userDto);

		User user = convertToEntity(userDto);

		logger.trace("Got user: {}", user);

		User newUser = userRepository.save(user);

		// TODO: Add link to me
		UserResource userResource = new UserResource(newUser);

		return ResponseEntity.created(URI.create(userResource.getLink("self").getHref())).build();
	}

	private void verifyInputData(UserDto userDto) {
		validatePasswords(userDto);
		
		validateEmailDoesntExist(userDto.getEmail());
	}

	private void validateEmailDoesntExist(String email) {
		if(userRepository.findByEmail(email).isPresent()) {
			throw new EmailAlreadyRegisterd();
		}
	}

	private void validatePasswords(UserDto userDto) {
		if (!userDto.getPassword().equals(userDto.getPasswordConfirmation())) {
			throw new PasswordsDontMatchException();
		}
	}

	private User convertToEntity(UserDto userDto) {
		return modelMapper.map(userDto, User.class);
	}
}
