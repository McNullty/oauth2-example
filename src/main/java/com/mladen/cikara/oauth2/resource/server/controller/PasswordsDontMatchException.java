package com.mladen.cikara.oauth2.resource.server.controller;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PasswordsDontMatchException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 925987266417146089L;

}
