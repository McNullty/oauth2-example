package com.mladen.cikara.oauth2.resource.server.controller;

import static io.restassured.RestAssured.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.snippet.Attributes.key;

import com.mladen.cikara.oauth2.authorization.server.security.model.RegisterUserDto;
import com.mladen.cikara.oauth2.util.DockerComposeRuleUtil;
import com.palantir.docker.compose.DockerComposeRule;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

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

  private static class ConstrainedFields {

    private final ConstraintDescriptions constraintDescriptions;
    private final String fieldPrefix;

    ConstrainedFields(final Class<?> input, final String fieldPrefix) {
      this.constraintDescriptions = new ConstraintDescriptions(input);
      this.fieldPrefix = fieldPrefix;
    }

    private FieldDescriptor withPath(final String path) {
      return fieldWithPath(this.fieldPrefix + "." + path)
          .attributes(key("constraints").value(StringUtils
              .collectionToDelimitedString(this.constraintDescriptions
                  .descriptionsForProperty(path), ". ")));
    }
  }

  private static final Logger logger = LoggerFactory.getLogger(RegisterActionIntTest.class);

  @ClassRule
  public static DockerComposeRule docker = DockerComposeRuleUtil.getDockerComposeRule();

  @BeforeClass
  public static void setupClass() throws InterruptedException {
    DockerComposeRuleUtil.setDatabaseUrlProperty(docker);
  }

  @Rule
  public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

  @LocalServerPort
  private int port;

  private RequestSpecification spec;

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
    given()
      .body(userJsonObj.toString())
      .contentType(ContentType.JSON)
      .log().all()
    .when()
      .post("/register")
    .then()
      .log().all()
      .statusCode(HttpStatus.BAD_REQUEST.value());
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

    final ConstrainedFields fields =
        new ConstrainedFields(RegisterUserDto.class, "user");

    // @formatter:off
    given(this.spec)
      .filter(document("register",
          requestFields(
              fields.withPath("firstName").description("The user's first name"),
              fields.withPath("lastName").description("The user's last name"),
              fields.withPath("email").description("The user's email"),
              fields.withPath("password").description("Password for new user"),
              fields.withPath("passwordConfirmation").description("Confirmation for password")
              ),
          responseHeaders(
              headerWithName("Location").description("HTTP location for newly created user"))
          ))
      .body(userJsonObj.toString())
      .contentType(ContentType.JSON)
      .log().all()
    .when()
      .post("/register")
    .then()
      .log().all()
      .statusCode(HttpStatus.CREATED.value());
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
    given()
      .body(userJsonObj.toString())
      .contentType(ContentType.JSON)
      .log().all()
    .when()
      .post("/register")
    .then()
      .log().all()
      .statusCode(HttpStatus.BAD_REQUEST.value());
    // @formatter:on
  }

  @Test
  public void whenPostEmptyEmail_thenBadRequest() throws Exception {
    final JSONObject jsonObj =
        new JSONObject()
            .put("email", "")
            .put("firstName", "TestName")
            .put("lastName", "TestSurname")
            .put("password", "secret")
            .put("passwordConfirmation", "secret");

    final JSONObject userJsonObj = new JSONObject().put("user", jsonObj);

    // @formatter:off
    given()
      .body(userJsonObj.toString())
      .contentType(ContentType.JSON)
      .log().all()
    .when()
      .post("/register")
    .then()
      .log().all()
      .statusCode(HttpStatus.BAD_REQUEST.value());
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
    given()
      .body(userJsonObj.toString())
      .contentType(ContentType.JSON)
      .log().all()
    .when()
      .post("/register")
    .then()
      .log().all()
      .statusCode(HttpStatus.BAD_REQUEST.value());
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
    given()
        .body(userJsonObj.toString())
        .contentType(ContentType.JSON)
        .log().all()
      .when()
        .post("/register")
      .then()
        .log().all()
        .statusCode(HttpStatus.BAD_REQUEST.value());
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
    given()
        .body(userJsonObj.toString())
        .contentType(ContentType.JSON)
        .log().all()
      .when()
        .post("/register")
      .then()
        .log().all()
        .statusCode(HttpStatus.BAD_REQUEST.value());
    // @formatter:on
  }
}
