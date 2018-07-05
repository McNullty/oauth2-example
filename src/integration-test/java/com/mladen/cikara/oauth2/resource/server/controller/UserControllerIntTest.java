package com.mladen.cikara.oauth2.resource.server.controller;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mladen.cikara.oauth2.authorization.server.security.model.RegisterUserDto;
import com.mladen.cikara.oauth2.authorization.server.security.model.User;
import com.mladen.cikara.oauth2.authorization.server.security.service.UserService;
import com.mladen.cikara.oauth2.util.DockerComposeRuleUtil;
import com.mladen.cikara.oauth2.util.OAuth2AuthorizationBuilder;
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
 * @author mladen
 *
 */
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DirtiesContext
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UserControllerIntTest {

  private static final String PASSWORD = "secret";

  private static final String LAST_NAME = "testLastName";

  private static final String FIRST_NAME = "testFirstName";

  private static final String EMAIL = "test@oauth2.com";

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
  private UserService userService;

  private User createNewUser() {
    final RegisterUserDto userDto = new RegisterUserDto.Builder()
        .email(EMAIL)
        .firstName(FIRST_NAME)
        .lastName(LAST_NAME)
        .password(PASSWORD)
        .passwordConfirmation(PASSWORD)
        .build();

    return userService.registerUser(userDto);
  }

  private String getAuthorization() throws Exception {
    // @formatter:off
    final String jwt =
        OAuth2AuthorizationBuilder
          .oauth2Request(mockMvc)
          .grantType("password")
          .accessTokenUrl("/oauth/token")
          .username(EMAIL)
          .password(PASSWORD)
          .clientId("d4486b29-7f28-43db-8d4e-44df6b5785c9")
          .clientSecret("a6f59937-fc55-485c-bf91-c8bcdaae2e45")
          .scope("web")
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
  public void whenGetMeWhenLoggedIn_thenOK() throws Exception {

    final User user = createNewUser();

    final String jwt = getAuthorization();

    logger.debug("Got authorization: {}", jwt);

    // @formatter:off
    final MvcResult response =
        given()
          .header("Authorization", "Bearer " + jwt)
          .log().all()
        .when()
          .get("/me")
        .then()
          .log().all()
          .statusCode(HttpStatus.OK.value())
          .body("email", equalTo(EMAIL))
          .body("firstName", equalTo(FIRST_NAME))
          .body("lastName", equalTo(LAST_NAME))
          .body("uuid", equalTo(user.getUUID().toString()))
          .extract().response()
          // TODO: add more assertions
          .mvcResult();
    // @formatter:on

    logger.debug("Response: {}", response);
  }

  @Test
  public void whenGetMeWithoutBeingLoggedIn_thenUnauthorized() throws Exception {
    // @formatter:off
    mockMvc
      .perform(get("/me"))
        .andDo(print())
        .andExpect(status().isUnauthorized());
    // @formatter:on
  }
}
