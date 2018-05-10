package com.mladen.cikara.oauth2.util;

import com.palantir.docker.compose.connection.DockerPort;
import com.palantir.docker.compose.connection.waiting.HealthCheck;
import com.palantir.docker.compose.connection.waiting.SuccessOrFailure;

public class HealthCheckUtil {
  public static HealthCheck<DockerPort> toBeOpen() {
    return port -> SuccessOrFailure.fromBoolean(port.isListeningNow(),
        "Internal port " + port + " was not listening");
  }

  private HealthCheckUtil() {
    throw new AssertionError();
  }
}
