package com.mladen.cikara.oauth2.resource.server.controller;

import java.util.Map;

import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

@Component
class ExceptionSupressingErrorAttributes extends DefaultErrorAttributes {

  @Override
  public Map<String, Object> getErrorAttributes(final WebRequest webRequest,
      final boolean includeStackTrace) {

    final Map<String, Object> errorAttributes =
        super.getErrorAttributes(webRequest, includeStackTrace);
    errorAttributes.remove("exception");

    final Object message =
        webRequest.getAttribute("javax.servlet.error.message", RequestAttributes.SCOPE_REQUEST);
    if (message != null) {
      errorAttributes.put("message", message);
    }

    return errorAttributes;
  }
}
