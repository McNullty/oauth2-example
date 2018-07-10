package com.mladen.cikara.oauth2.resource.server.controller;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;

import com.mladen.cikara.oauth2.authorization.server.security.model.Authority;
import com.mladen.cikara.oauth2.authorization.server.security.model.User;
import com.mladen.cikara.oauth2.authorization.server.security.service.AuthorizationsUtilService;
import com.mladen.cikara.oauth2.util.DockerComposeRuleUtil;
import com.palantir.docker.compose.DockerComposeRule;

import org.json.JSONException;
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

import io.restassured.module.mockmvc.RestAssuredMockMvc;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@DirtiesContext
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ChangePasswordActionTest {

  private static final String PASSWORD = "secret";
  private static final String NEW_PASSWORD = "newPassword";

  private static final Logger logger = LoggerFactory.getLogger(ChangePasswordActionTest.class);

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

  private String prepareChangePasswordDtoJsonObject() throws JSONException {
    final JSONObject changeObj =
        new JSONObject()
            .put("oldPassword", PASSWORD)
            .put("newPassword", NEW_PASSWORD)
            .put("newPasswordConfirmation", NEW_PASSWORD);

    final JSONObject jsonObj =
        new JSONObject()
            .put("changePassword", changeObj);

    return jsonObj.toString();
  }

  @Before
  public void setup() throws Exception {
    logger.debug("Configuring RestAssuredMockMvc");

    RestAssuredMockMvc.mockMvc(mockMvc);
  }

  @Test
  public void whenPostChangePassword_thenNoContent() throws Exception {
    final User tempUser = createNewUser();

    final String jwt = getAuthorization(tempUser);

    final String urlPath = "/change-password";

    final String changePasswordDtoJsonObject = prepareChangePasswordDtoJsonObject();

    // @formatter:off
    final MvcResult response =
        given()
          .header("Authorization", "Bearer " + jwt)
          .body(changePasswordDtoJsonObject)
          .contentType("application/json")
          .log().all()
        .when()
          .post(urlPath)
        .then()
          .log().all()
          .statusCode(HttpStatus.NO_CONTENT.value())
          .extract().response()
          .mvcResult();
    // @formatter:on

    logger.debug("Response: {}", response);
  }

}
