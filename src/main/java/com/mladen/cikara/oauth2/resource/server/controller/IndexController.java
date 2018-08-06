package com.mladen.cikara.oauth2.resource.server.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import org.springframework.hateoas.ResourceSupport;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class IndexController {

  /**
   * Index controller that documents API.
   *
   * @return Documentation about API
   */
  @RequestMapping(method = RequestMethod.GET)
  public ResourceSupport index() {
    final ResourceSupport index = new ResourceSupport();
    index.add(linkTo(RegisterAction.class).withRel("register")
        .withDeprecation("Endpoint for registering new user"));

    return index;
  }

}
