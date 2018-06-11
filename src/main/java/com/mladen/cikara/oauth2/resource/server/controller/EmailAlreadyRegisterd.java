package com.mladen.cikara.oauth2.resource.server.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EmailAlreadyRegisterd extends RuntimeException {

  private static final long serialVersionUID = -5226750731809539820L;

}
