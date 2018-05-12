package com.mladen.cikara.oauth2.resource.server.config;

import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

/**
 * This is a place for overriding default configuration for OAuth2 Resource
 * server. For reference you should check
 * {@link OAuth2ResourceServerConfiguration}
 *
 * @author Mladen Čikara <mladen.cikara@gmail.com>
 *
 */
@Configuration
@EnableResourceServer
public class OAuth2ResourceServerConfig extends ResourceServerConfigurerAdapter {

  @Override
  public void configure(HttpSecurity http) throws Exception {

// @formatter:off

    http.authorizeRequests()
      .antMatchers("/public").permitAll()
      .anyRequest().authenticated()
      ;

// @formatter:on

  }
}
