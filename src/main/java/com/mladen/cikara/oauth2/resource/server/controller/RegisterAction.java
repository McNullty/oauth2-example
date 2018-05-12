package com.mladen.cikara.oauth2.resource.server.controller;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.mladen.cikara.oauth2.authorization.server.security.model.User;
import com.mladen.cikara.oauth2.authorization.server.security.model.UserDto;

@RestController
public class RegisterAction {

	private static final Logger logger = LoggerFactory.getLogger(RegisterAction.class);
	
	private ModelMapper modelMapper;
	
	public RegisterAction(ModelMapper modelMapper) {
		this.modelMapper = modelMapper;
	}
	
	@PostMapping("/register")
	public ResponseEntity<?> registerUser(@Valid @RequestBody UserDto userDto) {
		logger.trace("Got user DTO: {}", userDto);
		
		validatePasswords(userDto);
		
		User user = convertToEntity(userDto);
		
		logger.trace("Got user: {}", user);

		return ResponseEntity.noContent().build();
	}

	private void validatePasswords(UserDto userDto) {
		if(!userDto.getPassword().equals(userDto.getPasswordConfirmation())) {
			throw new PasswordsDontMatchException();
		}
	}

	private User convertToEntity(UserDto userDto) {
		return modelMapper.map(userDto, User.class);
	}
}
