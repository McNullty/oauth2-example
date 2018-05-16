package com.mladen.cikara.oauth2.resource.server.controller;

import static org.assertj.core.api.Assertions.fail;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mladen.cikara.oauth2.util.DockerComposeRuleUtil;
import com.mladen.cikara.oauth2.util.OAuth2AuthorizationBuilder;
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

  private static final String PASSWORD = "secret";

  private static final String USERNAME = "user@oauth2.com";

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

  private String getJwt() throws Exception {
    // @formatter:off
		final String jwt =
		    OAuth2AuthorizationBuilder
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
