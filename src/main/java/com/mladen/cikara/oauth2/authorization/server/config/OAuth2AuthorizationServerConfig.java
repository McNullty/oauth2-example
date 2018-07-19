package com.mladen.cikara.oauth2.authorization.server.config;

import org.springframework.boot.autoconfigure.security.oauth2.authserver.OAuth2AuthorizationServerConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;

/**
 * This is a place for overriding default configuration for OAuth2 Authorization
 * Server. For reference you should check
 * {@link OAuth2AuthorizationServerConfiguration}.
 *
 * @author mladen
 *
 */
@Configuration
@EnableAuthorizationServer
public class OAuth2AuthorizationServerConfig {

}
