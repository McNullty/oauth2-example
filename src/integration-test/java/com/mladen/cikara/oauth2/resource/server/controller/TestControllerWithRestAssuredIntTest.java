package com.mladen.cikara.oauth2.resource.server.controller;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.CoreMatchers.containsString;

import com.mladen.cikara.oauth2.authorization.server.security.model.User;
import com.mladen.cikara.oauth2.authorization.server.security.service.AuthorizationsUtilService;
import com.mladen.cikara.oauth2.util.DockerComposeRuleUtil;
import com.palantir.docker.compose.DockerComposeRule;

import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;

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

@ActiveProfiles("test")
@AutoConfigureMockMvc
@DirtiesContext
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TestControllerWithRestAssuredIntTest {

  private static final Logger logger =
      LoggerFactory.getLogger(TestControllerWithRestAssuredIntTest.class);

  @ClassRule
  public static DockerComposeRule docker = DockerComposeRuleUtil.getDockerComposeRule();

  @BeforeClass
  public static void setupClass() throws InterruptedException {
    DockerComposeRuleUtil.setDatabaseUrlProperty(docker);
  }

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private AuthorizationsUtilService authorizationsUtilService;

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

    RestAssuredMockMvc.mockMvc(this.mockMvc);
  }

  @Test
  public void whenGetActuatorHealtNoAuthentication_thenUnauthorized() throws Exception {

    // @formatter:off
    final MvcResult response =
        given()
            .log().all()
          .when()
            .get("/actuator/health")
          .then()
            .log().all()
            .statusCode(HttpStatus.UNAUTHORIZED.value())
            .extract().response().mvcResult();

    logger.debug("Response: {}", response);
    // @formatter:on
  }

  @Test
  public void whenGetActuatorHealtWithAuthentication_thenOk() throws Exception {

    final String jwt = getJwt();

    logger.debug("JWT {}", jwt);

    // @formatter:off
    final MvcResult response =
        given()
            .header("Authorization", "Bearer " + jwt)
            .log().all()
          .when()
            .get("/actuator/health")
          .then()
            .log().all()
            .statusCode(HttpStatus.OK.value())
            .body("status", containsString("UP"))
            .extract().response().mvcResult();

    logger.debug("Response: {}", response);
    // @formatter:on
  }

  @Test
  public void whenGetPrivateHomeWithAuthentication_thenOk() throws Exception {

    final String jwt = getJwt();

    logger.debug("JWT {}", jwt);

    // @formatter:off
    final MvcResult response =
        given()
            .header("Authorization", "Bearer " + jwt)
            .log().all()
          .when()
            .get("/private")
          .then()
            .log().all()
            .statusCode(HttpStatus.OK.value())
            .contentType(ContentType.TEXT)
            .body(containsString("Hello from the private side"))
            .extract().response().mvcResult();

    logger.debug("Response: {}", response);
    // @formatter:on
  }

  @Test
  public void whenGetPrivateHomeWithNoAuthentication_thenUnauthorized() throws Exception {

    // @formatter:off
    final MvcResult response =
        given()
            .log().all()
          .when()
            .get("/private")
          .then()
            .log().all()
            .statusCode(HttpStatus.UNAUTHORIZED.value())
            .extract().response().mvcResult();

    logger.debug("Response: {}", response);
    // @formatter:on
  }

  @Test
  public void whenGetPublicHomeWithNoAuthentication_thenOk() throws Exception {

    // @formatter:off
    final MvcResult response =
        given()
            .log().all()
          .when()
            .get("/public")
          .then()
            .log().all()
            .statusCode(HttpStatus.OK.value())
            .contentType(ContentType.TEXT)
            .body(containsString("Hello from the public side"))
            .extract().response().mvcResult();

    logger.debug("Response: {}", response);
    // @formatter:on

  }

}
