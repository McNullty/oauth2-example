package com.mladen.cikara.oauth2.util;

import com.palantir.docker.compose.DockerComposeRule;
import com.palantir.docker.compose.connection.DockerPort;
import com.palantir.docker.compose.connection.waiting.HealthChecks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DockerComposeRuleUtil {

  private static final int DOCKER_INTERNAL_PORT = 5432;

  private static final Logger logger = LoggerFactory.getLogger(DockerComposeRuleUtil.class);

  private static final String DOCKER_COMPOSE_PATH = "src/test/resources/docker-compose-test.yml";
  private static final String DOCKER_CONTINER_NAME = "testdb";

  /**
   * Returns Docker Compose rule that will be used in integration tests.
   *
   * @return DockerComposeRule
   */
  public static DockerComposeRule getDockerComposeRule() {
    return DockerComposeRule.builder().file(DOCKER_COMPOSE_PATH)
        .waitingForService(DOCKER_CONTINER_NAME, HealthChecks.toHaveAllPortsOpen()).build();
  }

  /**
   * Sets database property for docker compose rule.
   *
   * @param docker
   *          Docker compose rule
   */
  public static void setDatabaseUrlProperty(final DockerComposeRule docker) {
    final DockerPort postgresPort =
        docker.containers().container(DOCKER_CONTINER_NAME).port(DOCKER_INTERNAL_PORT);

    logger.debug("Database port: {}", postgresPort);

    final String springDatabaseUrl = "jdbc:postgresql://localhost:" + postgresPort.getExternalPort()
        + "/oauth2-test";

    logger.debug("Database url: {}", springDatabaseUrl);

    System.setProperty("spring.datasource.url", springDatabaseUrl);
  }

  private DockerComposeRuleUtil() {
    throw new AssertionError();
  }
}
