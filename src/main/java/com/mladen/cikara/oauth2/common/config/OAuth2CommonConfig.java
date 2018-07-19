package com.mladen.cikara.oauth2.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

/**
 * This configuration should be present on both Authorization server and Resource server and it
 * should be synchronized between servers (same values on both servers).
 *
 * @author mladen
 *
 */
@Configuration
public class OAuth2CommonConfig {

  @Autowired
  private UserDetailsService userDetailsService;

  /**
   * Configuring Access Token Converter used by OAuth2.
   *
   * @return
   */
  @Bean
  public JwtAccessTokenConverter accessTokenConverter() {
    final JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
    jwtAccessTokenConverter.setSigningKey("some_key"); // TODO change key, Put in configuration
    ((DefaultAccessTokenConverter) jwtAccessTokenConverter.getAccessTokenConverter())
        .setUserTokenConverter(userAuthenticationConverter());

    return jwtAccessTokenConverter;
  }

  /**
   * Configuring User Authentication Converter used by OAuth2.
   * 
   * @return
   */
  @Bean
  public UserAuthenticationConverter userAuthenticationConverter() {
    final DefaultUserAuthenticationConverter defaultUserAuthenticationConverter =
        new DefaultUserAuthenticationConverter();
    defaultUserAuthenticationConverter.setUserDetailsService(this.userDetailsService);
    return defaultUserAuthenticationConverter;
  }

}
