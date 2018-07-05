package com.mladen.cikara.oauth2.resource.server.controller;

import static org.assertj.core.api.Assertions.fail;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mladen.cikara.oauth2.authorization.server.security.model.User;
import com.mladen.cikara.oauth2.authorization.server.security.service.AuthorizationsUtilService;
import com.mladen.cikara.oauth2.util.DockerComposeRuleUtil;
import com.palantir.docker.compose.DockerComposeRule;

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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@DirtiesContext
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TestControllerWithMockMvcIntTest {

  private static final Logger logger =
      LoggerFactory.getLogger(TestControllerWithMockMvcIntTest.class);

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
    final User user = authorizationsUtilService.getBasicUser();

    final String jwt = authorizationsUtilService.getAuthorizationJWT(user);

    return jwt;
  }

  @Test
  public void whenGetActuatorHealthWithAuthentication_thenOk() {

    String jwt;
    try {
      jwt = getJwt();

      logger.debug("JWT {}", jwt);

      // @formatter:off
			mockMvc
			  .perform(get("/actuator/health")
			      .header("Authorization", "Bearer " + jwt))
			      .andDo(print())
			      .andExpect(status().isOk())
			      .andExpect(jsonPath("$.status", is("UP")));
			// @formatter:on
    } catch (final Exception e) {
      fail("Failed getting JWT. {}", e.getMessage());
    }
  }

  @Test
  public void whenGetActuatorHealthWithNoAuthentication_thenUnauthorized() throws Exception {
    // @formatter:off
		mockMvc
		  .perform(get("/actuator/health"))
		    .andDo(print())
		    .andExpect(status().isUnauthorized());
		// @formatter:on
  }

  @Test
  public void whenGetPrivateHomeWithAuthentication_thenOk() throws Exception {

    final String jwt = getJwt();

    logger.debug("JWT {}", jwt);

    // @formatter:off
		mockMvc
		  .perform(get("/private")
		      .header("Authorization", "Bearer " + jwt))
		      .andDo(print())
		      .andExpect(status().isOk())
		      .andExpect(content().string(containsString("Hello from the private side")));
		// @formatter:on
  }

  @Test
  public void whenGetPrivateHomeWithNoAuthentication_thenUnauthorized() throws Exception {
    // @formatter:off
		mockMvc
		  .perform(get("/private"))
		  .andDo(print())
		  .andExpect(status().isUnauthorized());
		// @formatter:on
  }

  @Test
  public void whenGetPublicHomeWithNoAuthentication_thenOk() throws Exception {
    // @formatter:off
		mockMvc
		  .perform(get("/public"))
		  .andDo(print())
		  .andExpect(status().isOk())
		  .andExpect(content().string(containsString("Hello from the public side")));
		// @formatter:on
  }
}
