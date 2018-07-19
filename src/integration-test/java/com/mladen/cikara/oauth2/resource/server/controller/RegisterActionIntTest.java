package com.mladen.cikara.oauth2.resource.server.controller;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;

import com.mladen.cikara.oauth2.util.DockerComposeRuleUtil;
import com.palantir.docker.compose.DockerComposeRule;

import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;

import org.json.JSONObject;
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

/**
 * This integration test tests registering new user.
 *
 * @author mladen
 *
 */
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DirtiesContext
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class RegisterActionIntTest {

  private static final Logger logger = LoggerFactory.getLogger(RegisterActionIntTest.class);

  @ClassRule
  public static DockerComposeRule docker = DockerComposeRuleUtil.getDockerComposeRule();

  @BeforeClass
  public static void setupClass() throws InterruptedException {
    DockerComposeRuleUtil.setDatabaseUrlProperty(docker);
  }

  @Autowired
  private MockMvc mockMvc;

  /**
   * Setup test.
   */
  @Before
  public void setup() throws Exception {
    logger.debug("Configuring RestAssuredMockMvc");

    RestAssuredMockMvc.mockMvc(this.mockMvc);
  }

  @Test
  public void whenPostCorrectUserDataThatAlreadyExists_thenBadRequest() throws Exception {
    final JSONObject jsonObj =
        new JSONObject()
            .put("email", "admin@oauth2.com")
            .put("firstName", "TestName")
            .put("lastName", "TestSurname")
            .put("password", "secret")
            .put("passwordConfirmation", "secret");

    final JSONObject userJsonObj = new JSONObject().put("user", jsonObj);

    // @formatter:off
    final MvcResult response =
        given()
          .body(userJsonObj.toString())
          .contentType(ContentType.JSON)
          .log().all()
        .when()
          .post("/register")
        .then()
          .log().all()
          .statusCode(HttpStatus.BAD_REQUEST.value())
          .extract().response().mvcResult();

    logger.debug("Response: {}", response);
    // @formatter:on
  }

  @Test
  public void whenPostCorrectUserDataWithConfirmedPassword_thenCreated() throws Exception {
    final JSONObject jsonObj =
        new JSONObject()
            .put("email", "test@test.org")
            .put("firstName", "TestName")
            .put("lastName", "TestSurname")
            .put("password", "secret")
            .put("passwordConfirmation", "secret");

    final JSONObject userJsonObj = new JSONObject().put("user", jsonObj);

    // @formatter:off
    final MvcResult response =
        given()
          .body(userJsonObj.toString())
          .contentType(ContentType.JSON)
          .log().all()
        .when()
          .post("/register")
        .then()
          .log().all()
          .statusCode(HttpStatus.CREATED.value())
          .extract().response().mvcResult();

    logger.debug("Response: {}", response);
    // @formatter:on
  }

  @Test
  public void whenPostCorrectUserDataWithouthMatchingPasswords_thenBadRequest() throws Exception {
    final JSONObject jsonObj =
        new JSONObject()
            .put("email", "test@test.org")
            .put("firstName", "TestName")
            .put("lastName", "TestSurname")
            .put("password", "secret")
            .put("passwordConfirmation", "terces");

    final JSONObject userJsonObj = new JSONObject().put("user", jsonObj);

    // @formatter:off
    final MvcResult response =
        given()
          .body(userJsonObj.toString())
          .contentType(ContentType.JSON)
          .log().all()
        .when()
          .post("/register")
        .then()
          .log().all()
          .statusCode(HttpStatus.BAD_REQUEST.value())
          .extract().response().mvcResult();

    logger.debug("Response: {}", response);
    // @formatter:on
  }

  @Test
  public void whenPostFieldWitheEmptyString_thenBadRequest() throws Exception {
    final JSONObject jsonObj =
        new JSONObject()
            .put("email", "test@test.org")
            .put("firstName", "")
            .put("lastName", "TestSurname")
            .put("password", "secret")
            .put("passwordConfirmation", "secret");

    final JSONObject userJsonObj = new JSONObject().put("user", jsonObj);

    // @formatter:off
    final MvcResult response =
        given()
            .body(userJsonObj.toString())
            .contentType(ContentType.JSON)
            .log().all()
          .when()
            .post("/register")
          .then()
            .log().all()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .extract().response().mvcResult();

    logger.debug("Response: {}", response);
    // @formatter:on
  }

  @Test
  public void whenPostInvalidEmail_thenBadRequest() throws Exception {
    final JSONObject jsonObj =
        new JSONObject()
            .put("email", "test")
            .put("firstName", "TestName")
            .put("lastName", "TestSurname")
            .put("password", "secret")
            .put("passwordConfirmation", "secret");

    final JSONObject userJsonObj = new JSONObject().put("user", jsonObj);

    // @formatter:off
    final MvcResult response =
        given()
            .body(userJsonObj.toString())
            .contentType(ContentType.JSON)
            .log().all()
          .when()
            .post("/register")
          .then()
            .log().all()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .extract().response().mvcResult();

    logger.debug("Response: {}", response);
    // @formatter:on
  }

  @Test
  public void whenPostMisssingField_thenBadRequest() throws Exception {
    final JSONObject jsonObj =
        new JSONObject()
            .put("email", "test@test.org")
            .put("firstName", "TestName")
            .put("password", "secret")
            .put("passwordConfirmation", "secret");

    final JSONObject userJsonObj = new JSONObject().put("user", jsonObj);

    // @formatter:off
    final MvcResult response =
        given()
            .body(userJsonObj.toString())
            .contentType(ContentType.JSON)
            .log().all()
          .when()
            .post("/register")
          .then()
            .log().all()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .extract().response().mvcResult();

    logger.debug("Response: {}", response);
    // @formatter:on
  }
}
