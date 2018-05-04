package com.mladen.cikara.oauth2.resource.server.controller;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.CoreMatchers.containsString;

import com.mladen.cikara.oauth2.util.OAuth2AuthorizationBuilder;
import com.palantir.docker.compose.DockerComposeRule;
import com.palantir.docker.compose.connection.DockerPort;
import com.palantir.docker.compose.connection.waiting.HealthChecks;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@DirtiesContext
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TestControllerWithRestAssuredIntTest {

  private static final String PASSWORD = "secret";

  private static final String USERNAME = "user@oauth2.com";

  private static final Logger logger =
      LoggerFactory.getLogger(TestControllerWithRestAssuredIntTest.class);

  @ClassRule
  public static DockerComposeRule docker =
      DockerComposeRule.builder()
          .file("src/test/resources/docker-compose-test.yml")
          .waitingForService("testdb", HealthChecks.toHaveAllPortsOpen())
          .build();

  @BeforeClass
  public static void setupClass() throws InterruptedException {
    // Thread.sleep(50l);

    final DockerPort postgresPort = docker.containers()
        .container("testdb")
        .port(5432);

    logger.debug("Database port: {}", postgresPort);

    final String springDatabaseUrl =
        "jdbc:postgresql://localhost:" + postgresPort.getExternalPort() + "/oauth2-test";

    logger.debug("Database url: {}", springDatabaseUrl);

    System.setProperty("spring.datasource.url", springDatabaseUrl);
  }

  @Autowired
  private MockMvc mockMvc;

  private String getJwt() throws Exception {
// @formatter:off
    final String jwt = OAuth2AuthorizationBuilder
        .oauth2Request(mockMvc)
          .grantType("password")
          .accessTokenUrl("/oauth/token")
          .username(USERNAME)
          .password(PASSWORD)
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
