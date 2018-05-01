package com.mladen.cikara.oauth2.resource.server.controller;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.CoreMatchers.containsString;

import com.mladen.cikara.oauth2.util.OAuth2AuthorizationBuilder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestControllerWithRestAssuredIntTest {

  private static final Logger logger =
      LoggerFactory.getLogger(TestControllerWithRestAssuredIntTest.class);

  @Autowired
  private MockMvc mockMvc;

  private String getJwt() throws Exception {
// @formatter:off
    final String jwt = OAuth2AuthorizationBuilder
        .oauth2Request(mockMvc)
          .grantType("password")
          .accessTokenUrl("/oauth/token")
          .username("user")
          .password("44947188-6f69-44f9-a8c3-315dab31ec89")
          .clientId("d4486b29-7f28-43db-8d4e-44df6b5785c9")
          .clientSecret("a6f59937-fc55-485c-bf91-c8bcdaae2e45")
          .scope("bla")
          .getAccessToken();
// @formatter:on

    return jwt;
  }

  @Before
  public void setup() throws Exception {
    logger.debug("Configuring RestAssuredMockMvc");

    RestAssuredMockMvc.mockMvc(mockMvc);
  }

  @Test
  public void whenGetActuatorHealtNoAuthentication_thenUnauthorized() throws Exception {

// @formatter:off
    final MvcResult response =
        given().
          log().all().
        when().
          get("/actuator/health").
        then().
          log().all().
          statusCode(HttpStatus.UNAUTHORIZED.value()).
          extract().response().mvcResult()
          ;

      logger.debug("Response: {}", response);
// @formatter:on
  }

  @Test
  public void whenGetActuatorHealtWithAuthentication_thenOk() throws Exception {

    final String jwt = getJwt();

    logger.debug("JWT {}", jwt);

// @formatter:off
    final MvcResult response =
        given().
          header("Authorization", "Bearer " + jwt).
          log().all().
        when().
          get("/actuator/health").
        then().
          log().all().
          statusCode(HttpStatus.OK.value()).
          body("status", containsString("UP")).
          extract().response().mvcResult()
          ;

      logger.debug("Response: {}", response);
// @formatter:on
  }

  @Test
  public void whenGetPrivateHomeWithAuthentication_thenOk() throws Exception {

    final String jwt = getJwt();

    logger.debug("JWT {}", jwt);

// @formatter:off
    final MvcResult response =
        given().
          header("Authorization", "Bearer " + jwt).
          log().all().
        when().
          get("/private").
        then().
          log().all().
          statusCode(HttpStatus.OK.value()).
          contentType(ContentType.TEXT).
          body(containsString("Hello from the private side")).
          extract().response().mvcResult()
          ;

      logger.debug("Response: {}", response);
// @formatter:on
  }

  @Test
  public void whenGetPrivateHomeWithNoAuthentication_thenUnauthorized() throws Exception {

// @formatter:off
    final MvcResult response =
        given().
          log().all().
        when().
          get("/private").
        then().
          log().all().
          statusCode(HttpStatus.UNAUTHORIZED.value()).
          extract().response().mvcResult()
          ;

      logger.debug("Response: {}", response);
// @formatter:on
  }

  @Test
  public void whenGetPublicHomeWithNoAuthentication_thenOk() throws Exception {

// @formatter:off
    final MvcResult response =
      given().
        log().all().
      when().
        get("/public").
      then().
        log().all().
        statusCode(HttpStatus.OK.value()).
        contentType(ContentType.TEXT).
        body(containsString("Hello from the public side")).
        extract().response().mvcResult()
        ;

    logger.debug("Response: {}", response);
// @formatter:on

  }

}
