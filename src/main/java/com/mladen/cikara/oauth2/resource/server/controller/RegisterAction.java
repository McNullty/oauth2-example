package com.mladen.cikara.oauth2.resource.server.controller;

import java.net.URI;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.mladen.cikara.oauth2.authorization.server.security.model.User;
import com.mladen.cikara.oauth2.authorization.server.security.model.UserDto;
import com.mladen.cikara.oauth2.authorization.server.security.model.UserResource;
import com.mladen.cikara.oauth2.authorization.server.security.service.UserService;

@RestController
public class RegisterAction {

	private static final Logger logger = LoggerFactory.getLogger(RegisterAction.class);

	private UserService userService;
	
	public RegisterAction(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/register")
	public ResponseEntity<?> registerUser(@Valid @RequestBody UserDto userDto) {

		logger.trace("Got user DTO: {}", userDto);

		User newUser = userService.registerUser(userDto);

		logger.trace("Registered user: {}", newUser);

		// TODO: Add link to me
		UserResource userResource = new UserResource(newUser);

		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(userResource.getId()).toUri();

		// TODO: Add location to response
		return ResponseEntity.created(location).build();
	}
}
