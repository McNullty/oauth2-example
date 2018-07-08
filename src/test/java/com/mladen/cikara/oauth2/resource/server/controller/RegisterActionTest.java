package com.mladen.cikara.oauth2.resource.server.controller;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import com.mladen.cikara.oauth2.authorization.server.security.model.RegisterUserDto;
import com.mladen.cikara.oauth2.authorization.server.security.model.User;
import com.mladen.cikara.oauth2.authorization.server.security.service.UserService;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest(RegisterAction.class)
public class RegisterActionTest {

  private static final Logger logger = LoggerFactory.getLogger(RegisterActionTest.class);

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userServiceMock;

  @Before
  public void setup() throws Exception {
    RestAssuredMockMvc.postProcessors(csrf().asHeader());
    RestAssuredMockMvc.mockMvc(mockMvc);
  }

  @Test
  @WithMockUser
  public void whenPostCorrectUserDataWithConfirmedPassword_thenCreated() throws Exception {

    when(userServiceMock.registerUser(any(RegisterUserDto.class)))
        .thenReturn(new User.Builder().password("password").build());

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
    // @formatter:on

    // TODO: Add assert that checks HATEOS object

    logger.debug(response.getResponse().getContentAsString());
  }

}
