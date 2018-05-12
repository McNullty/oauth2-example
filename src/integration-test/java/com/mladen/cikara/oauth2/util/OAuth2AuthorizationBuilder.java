package com.mladen.cikara.oauth2.util;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.springframework.security.oauth2.common.util.JacksonJsonParser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Builder for creating requet to get JWD token from OAuth2 Autorization server.
 *
 * @author Mladen Čikara <mladen.cikara@gmail.com>
 *
 */
public class OAuth2AuthorizationBuilder {

  public static OAuth2AuthorizationBuilder oauth2Request(MockMvc mockMvc) {
    return new OAuth2AuthorizationBuilder(mockMvc);
  }

  private String username;

  private String password;

  private String clientId;

  private String clientSecret;

  private String grantType;

  private String scope;

  private String accessTokenUrl = "/oauth/token";

  private final MockMvc mockMvc;

  OAuth2AuthorizationBuilder(MockMvc mockMvc) {
    this.mockMvc = mockMvc;
  }

  public OAuth2AuthorizationBuilder accessTokenUrl(String accessTokenUrl) {
    this.accessTokenUrl = accessTokenUrl;

    return this;
  }

  public OAuth2AuthorizationBuilder clientId(String clientId) {
    this.clientId = clientId;

    return this;
  }

  public OAuth2AuthorizationBuilder clientSecret(String clientSecret) {
    this.clientSecret = clientSecret;

    return this;
  }

  public String getAccessToken() throws Exception {
    final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("grant_type", grantType);
    params.add("client_id", clientId);
    params.add("username", username);
    params.add("password", password);
    params.add("scope", scope);

    final ResultActions result = mockMvc.perform(post(accessTokenUrl)
        .params(params)
        .with(httpBasic(clientId, clientSecret))
        .accept("application/json;charset=UTF-8"));

    final String resultString = result.andReturn().getResponse().getContentAsString();

    final JacksonJsonParser jsonParser = new JacksonJsonParser();

    return jsonParser.parseMap(resultString).get("access_token").toString();
  }

  public OAuth2AuthorizationBuilder grantType(String grantType) {
    this.grantType = grantType;

    return this;
  }

  public OAuth2AuthorizationBuilder password(String password) {
    this.password = password;

    return this;
  }

  public OAuth2AuthorizationBuilder scope(String scope) {
    this.scope = scope;

    return this;
  }

  public OAuth2AuthorizationBuilder username(String username) {
    this.username = username;

    return this;
  }
}
