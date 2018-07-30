package com.mladen.cikara.oauth2.resource.server.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration;

import com.mladen.cikara.oauth2.util.DockerComposeRuleUtil;
import com.palantir.docker.compose.DockerComposeRule;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

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
import org.springframework.restdocs.restassured3.RestDocumentationFilter;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@DirtiesContext
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class IndexControllerTest {

  private static final Logger logger = LoggerFactory.getLogger(IndexControllerTest.class);

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
  private RestDocumentationFilter documentFilter;

  @Test
  public void headersExample() {
    // @formatter:off
    given(this.spec)
      .filter(this.documentFilter.document(
          responseHeaders(
              headerWithName("Content-Type").description(
                  "The Content-Type of the payload, e.g. `application/hal+json`"))
          ))
      .contentType(ContentType.JSON)
      .log().all()
    .when()
      .get("/")
    .then()
      .log().all()
      .statusCode(HttpStatus.OK.value())
      .header("Content-Type", startsWith("application/hal+json"))
      ;
    // @formatter:on
  }

  /**
   * Setup test.
   */
  @Before
  public void setup() throws Exception {
    logger.debug("Configuring RestAssuredMockMvc");

    RestAssured.port = this.port;
    RestAssured.baseURI = "https://localhost";
    RestAssured.useRelaxedHTTPSValidation();

    this.documentFilter = document("{method-name}",
        preprocessRequest(prettyPrint()),
        preprocessResponse(prettyPrint()));

    this.spec = new RequestSpecBuilder()
        .addFilter(documentationConfiguration(this.restDocumentation))
        .addFilter(
            this.documentFilter)
        .build();
  }

}
