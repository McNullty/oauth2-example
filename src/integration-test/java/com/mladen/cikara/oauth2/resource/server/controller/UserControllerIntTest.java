package com.mladen.cikara.oauth2.resource.server.controller;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mladen.cikara.oauth2.authorization.server.security.model.Authority;
import com.mladen.cikara.oauth2.authorization.server.security.model.User;
import com.mladen.cikara.oauth2.authorization.server.security.service.AuthorizationsUtilService;
import com.mladen.cikara.oauth2.util.DockerComposeRuleUtil;
import com.palantir.docker.compose.DockerComposeRule;

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

import io.restassured.module.mockmvc.RestAssuredMockMvc;

/**
 * This integration test tests all user end points
 *
 * @author Mladen Čikara mladen.cikara@gmail.com
 *
 */
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DirtiesContext
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UserControllerIntTest {

  private static final Logger logger = LoggerFactory.getLogger(UserControllerIntTest.class);

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

  private User createNewUser() {
    return authorizationsUtilService.createTempUserWithAuthorities(Authority.ROLE_USER);
  }

  private String getAuthorization(User user) throws Exception {
    return authorizationsUtilService.getAuthorizationJWT(user);
  }

  @Before
  public void setup() throws Exception {
    logger.debug("Configuring RestAssuredMockMvc");

    RestAssuredMockMvc.mockMvc(mockMvc);
  }

  @Test
  public void whenGetCurrentUserWhenLoggedIn_thenOK() throws Exception {

    final User user = createNewUser();

    final String jwt = getAuthorization(user);

    logger.debug("Got authorization: {}", jwt);

    // @formatter:off
    final MvcResult response =
        given()
          .header("Authorization", "Bearer " + jwt)
          .log().all()
        .when()
          .get("/user/current")
        .then()
          .log().all()
          .statusCode(HttpStatus.OK.value())
          .body("email", equalTo(user.getEmail()))
          .body("firstName", equalTo(user.getFirstName()))
          .body("lastName", equalTo(user.getLastName()))
          .body("uuid", equalTo(user.getUUID().toString()))
          .body("_links.self.href", equalTo("http://localhost/user/" + user.getUUID().toString()))
          .extract().response()
          .mvcResult();
    // @formatter:on

    logger.debug("Response: {}", response);
  }

  @Test
  public void whenGetCurrentUserWithoutBeingLoggedIn_thenUnauthorized() throws Exception {
    // @formatter:off
    mockMvc
      .perform(get("/user/current"))
        .andDo(print())
        .andExpect(status().isUnauthorized());
    // @formatter:on
  }

  @Test
  public void whenGetUsersWithoutBeingLoggedIn_thenUnauthorized() throws Exception {
    // @formatter:off
    final MvcResult response =
        given()
          .log().all()
        .when()
          .get("/user")
        .then()
          .log().all()
          .statusCode(HttpStatus.UNAUTHORIZED.value())
          .extract().response()
          .mvcResult();
    // @formatter:on
    logger.debug("Response: {}", response);
  }

  @Test
  public void whenGetUserWhenLogggedInWithAdmin_ThenOK() throws Exception {
    final String jwt = getAuthorization(authorizationsUtilService.getAdminUser());

    final String urlPath = "/user";

    // @formatter:off
    final MvcResult response =
        given()
          .header("Authorization", "Bearer " + jwt)
          .log().all()
        .when()
          .get(urlPath)
        .then()
          .log().all()
          .statusCode(HttpStatus.OK.value())
          .extract().response()
          .mvcResult();
    // @formatter:on

    logger.debug("Response: {}", response);
  }

  @Test
  public void whenGetUserWhenLogggedInWithBasicUser_ThenOkWithOnlyOneUserInList()
      throws Exception {
    final User tempUser = createNewUser();

    final String jwt = getAuthorization(tempUser);

    final String urlPath = "/user";

    // @formatter:off
    final MvcResult response =
        given()
          .header("Authorization", "Bearer " + jwt)
          .log().all()
        .when()
          .get(urlPath)
        .then()
          .log().all()
          .statusCode(HttpStatus.OK.value())
          .body("content[0].uuid", equalTo(tempUser.getUUID().toString()))
          .body("totalElements", equalTo(1))
          .extract().response()
          .mvcResult();
    // @formatter:on

    logger.debug("Response: {}", response);
  }

  @Test
  public void whenGetUserWithExistingUUIDAndLogggedInWithAdmin_ThenOK() throws Exception {
    final User tempUser = createNewUser();

    final String jwt = getAuthorization(authorizationsUtilService.getAdminUser());

    final String urlPath = "/user/" + tempUser.getUUID().toString();

    // @formatter:off
    final MvcResult response =
        given()
          .header("Authorization", "Bearer " + jwt)
          .log().all()
        .when()
          .get(urlPath)
        .then()
          .log().all()
          .statusCode(HttpStatus.OK.value())
          .body("email", equalTo(tempUser.getEmail()))
          .body("firstName", equalTo(tempUser.getFirstName()))
          .body("lastName", equalTo(tempUser.getLastName()))
          .body("uuid", equalTo(tempUser.getUUID().toString()))
          .body("_links.self.href", equalTo("http://localhost/user/" + tempUser.getUUID().toString()))
          .extract().response()
          .mvcResult();
    // @formatter:on

    logger.debug("Response: {}", response);
  }

  @Test
  public void whenGetUserWithExistingUUIDAndLogggedInWithBasicUser_ThenUnauthorized()
      throws Exception {
    final User tempUser = createNewUser();

    final String jwt = getAuthorization(authorizationsUtilService.getBasicUser());

    final String urlPath = "/user/" + tempUser.getUUID().toString();

    // @formatter:off
    final MvcResult response =
        given()
          .header("Authorization", "Bearer " + jwt)
          .log().all()
        .when()
          .get(urlPath)
        .then()
          .log().all()
          .statusCode(HttpStatus.UNAUTHORIZED.value())
          .extract().response()
          .mvcResult();
    // @formatter:on

    logger.debug("Response: {}", response);
  }

  @Test
  public void whenGetUserWithUUIDOfCurrentUser_ThenOK() throws Exception {
    final User user = createNewUser();

    final String jwt = getAuthorization(user);

    logger.debug("Got authorization: {}", jwt);

    final String urlPath = "/user/" + user.getUUID().toString();

    // @formatter:off
    final MvcResult response =
        given()
          .header("Authorization", "Bearer " + jwt)
          .log().all()
        .when()
          .get(urlPath)
        .then()
          .log().all()
          .statusCode(HttpStatus.OK.value())
          .body("email", equalTo(user.getEmail()))
          .body("firstName", equalTo(user.getFirstName()))
          .body("lastName", equalTo(user.getLastName()))
          .body("uuid", equalTo(user.getUUID().toString()))
          .body("_links.self.href", equalTo("http://localhost/user/" + user.getUUID().toString()))
          .extract().response()
          .mvcResult();
    // @formatter:on

    logger.debug("Response: {}", response);
  }
}
