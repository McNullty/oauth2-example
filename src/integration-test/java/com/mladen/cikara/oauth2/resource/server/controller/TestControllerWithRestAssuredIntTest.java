package com.mladen.cikara.oauth2.resource.server.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration;

import com.mladen.cikara.oauth2.authorization.server.security.model.User;
import com.mladen.cikara.oauth2.authorization.server.security.service.AuthorizationsUtilService;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@ActiveProfiles("int-test")
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TestControllerWithRestAssuredIntTest {

  private static final Logger logger =
      LoggerFactory.getLogger(TestControllerWithRestAssuredIntTest.class);

  @Rule
  public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

  @LocalServerPort
  private int port;

  @Autowired
  private AuthorizationsUtilService authorizationsUtilService;

  private RequestSpecification spec;

  private String getJwt() throws Exception {
    final User user = this.authorizationsUtilService.getBasicUser();

    final String jwt = this.authorizationsUtilService.getAuthorizationJwt(user);

    return jwt;
  }

  /**
   * Setup test.
   */
  @Before
  public void setup() throws Exception {
    logger.debug("Configuring RestAssuredMockMvc");

    RestAssured.port = this.port;
    RestAssured.baseURI = "https://localhost";
    RestAssured.useRelaxedHTTPSValidation();

    this.spec = new RequestSpecBuilder().addFilter(
        documentationConfiguration(this.restDocumentation))
        .build();
  }

  @Test
  public void whenGetActuatorHealtNoAuthentication_thenUnauthorized() throws Exception {

    // @formatter:off
    given(this.spec)
        .log().all()
      .when()
        .get("/actuator/health")
      .then()
        .log().all()
        .statusCode(HttpStatus.UNAUTHORIZED.value());

    // @formatter:on
  }

  @Test
  public void whenGetActuatorHealtWithAuthentication_thenOk() throws Exception {

    final String jwt = getJwt();

    logger.debug("JWT {}", jwt);

    // @formatter:off
    given(this.spec)
        .accept(ContentType.JSON)
        .filter(document("actuator/health"))
        .header("Authorization", "Bearer " + jwt)
        .log().all()
      .when()
        .get("/actuator/health")
      .then()
        .log().all()
        .statusCode(HttpStatus.OK.value())
        .body("status", containsString("UP"));

    // @formatter:on
  }

  @Test
  public void whenGetPrivateHomeWithAuthentication_thenOk() throws Exception {

    final String jwt = getJwt();

    logger.debug("JWT {}", jwt);

    // @formatter:off
    given(this.spec)
        .accept(ContentType.TEXT)
        .filter(document("private-test"))
        .header("Authorization", "Bearer " + jwt)
        .log().all()
      .when()
        .get("/private")
      .then()
        .log().all()
        .statusCode(HttpStatus.OK.value())
        .contentType(ContentType.TEXT)
        .body(containsString("Hello from the private side"));

    // @formatter:on
  }

  @Test
  public void whenGetPrivateHomeWithNoAuthentication_thenUnauthorized() throws Exception {

    // @formatter:off
    given(this.spec)
        .accept(ContentType.JSON)
        .filter(document("private-test-1"))
        .log().all()
      .when()
        .get("/private")
      .then()
        .log().all()
        .statusCode(HttpStatus.UNAUTHORIZED.value());

    // @formatter:on
  }

  @Test
  public void whenGetPublicHomeWithNoAuthentication_thenOk() throws Exception {

    // @formatter:off
    given(this.spec)
        .accept(ContentType.TEXT)
        .filter(document("public-test"))
        .log().all()
      .when()
        .get("/public")
      .then()
        .log().all()
        .statusCode(HttpStatus.OK.value())
        .contentType(ContentType.TEXT)
        .body(containsString("Hello from the public side"));

    // @formatter:on

  }

}
