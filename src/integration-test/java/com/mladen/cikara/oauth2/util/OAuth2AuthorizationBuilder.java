package com.mladen.cikara.oauth2.util;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.springframework.security.oauth2.common.util.JacksonJsonParser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Builder for creating request to get JWD token from OAuth2 Autorization server.
 *
 * @author mladen
 *
 */
public class OAuth2AuthorizationBuilder {

  public static OAuth2AuthorizationBuilder oauth2Request(final MockMvc mockMvc) {
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

  OAuth2AuthorizationBuilder(final MockMvc mockMvc) {
    this.mockMvc = mockMvc;
  }

  /**
   * Set access token url.
   *
   * @param accessTokenUrl
   *          Access token url.
   *
   * @return
   */
  public OAuth2AuthorizationBuilder accessTokenUrl(final String accessTokenUrl) {
    this.accessTokenUrl = accessTokenUrl;

    return this;
  }

  /**
   * Oauth2 client id.
   *
   * @param clientId
   *          Client Id
   * @return
   */
  public OAuth2AuthorizationBuilder clientId(final String clientId) {
    this.clientId = clientId;

    return this;
  }

  /**
   * Oauth2 Client secret.
   *
   * @param clientSecret
   *          Client secret
   * @return
   */
  public OAuth2AuthorizationBuilder clientSecret(final String clientSecret) {
    this.clientSecret = clientSecret;

    return this;
  }

  /**
   * Getter for Oauth2 Access Token.
   *
   * @return
   */
  public String getAccessToken() throws Exception {
    final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("grant_type", this.grantType);
    params.add("client_id", this.clientId);
    params.add("username", this.username);
    params.add("password", this.password);
    params.add("scope", this.scope);

    final ResultActions result = this.mockMvc.perform(post(this.accessTokenUrl)
        .params(params)
        .with(httpBasic(this.clientId, this.clientSecret))
        .accept("application/json;charset=UTF-8"));

    final String resultString = result.andReturn().getResponse().getContentAsString();

    final JacksonJsonParser jsonParser = new JacksonJsonParser();

    return jsonParser.parseMap(resultString).get("access_token").toString();
  }

  /**
   * Oauth2 Grant type.
   *
   * @param grantType
   *          grant type
   * @return
   */
  public OAuth2AuthorizationBuilder grantType(final String grantType) {
    this.grantType = grantType;

    return this;
  }

  /**
   * Oauth2 password.
   *
   * @param password
   *          password in clear text
   * @return
   */
  public OAuth2AuthorizationBuilder password(final String password) {
    this.password = password;

    return this;
  }

  /**
   * OAuth2 scope.
   *
   * @param scope
   *          Scope
   * @return
   */
  public OAuth2AuthorizationBuilder scope(final String scope) {
    this.scope = scope;

    return this;
  }

  /**
   * OAuth2 username.
   *
   * @param username
   *          Username
   * @return
   */
  public OAuth2AuthorizationBuilder username(final String username) {
    this.username = username;

    return this;
  }
}
