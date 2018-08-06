package com.mladen.cikara.oauth2.resource.server.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.halLinks;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = { IndexController.class })
public class IndexControllerTest {
  @Rule
  public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

  @Autowired
  private IndexController indexController;

  @Autowired
  private WebApplicationContext context;

  private MockMvc mockMvc;

  /**
   * Setup MockMvc for documenting API.
   */
  @Before
  public void setUp() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
        .apply(documentationConfiguration(this.restDocumentation))
        .build();
  }

  @Test
  public void smokeTest() {
    assertThat(this.indexController).isNotNull();
  }

  /**
   * Testing index endpoint (/).
   *
   * @throws Exception
   *           Exception thrown by mockMvc
   */
  @Test
  public void whenGetIndexController_thenOk() throws Exception {
    // @formatter:off
    this.mockMvc
        .perform(get("/")
            .accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andDo(document("index", links(halLinks(),
            linkWithRel("register").description("Endpoint for registering new user"))));
    // @formatter:on
  }
}
