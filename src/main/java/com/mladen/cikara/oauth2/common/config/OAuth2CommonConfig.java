package com.mladen.cikara.oauth2.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

/**
 * This configuration should be present on both Authorization server and
 * Resource server and it should be synchronized between servers (same values on
 * both servers).
 *
 * @author Mladen Čikara <mladen.cikara@gmail.com>
 *
 */
@Configuration
public class OAuth2CommonConfig {

  @Bean
  public JwtAccessTokenConverter accessTokenConverter() {
    return new JwtAccessTokenConverter();
  }

}
