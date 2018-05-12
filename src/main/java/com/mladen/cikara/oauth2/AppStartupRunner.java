package com.mladen.cikara.oauth2;

import javax.annotation.Priority;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Priority(value = Integer.MAX_VALUE)
@Component
public class AppStartupRunner implements ApplicationRunner {

  private static final Logger logger = LoggerFactory.getLogger(AppStartupRunner.class);

  @Override
  public void run(ApplicationArguments args) throws Exception {
    logger.info("Your application started with option names : {}", args.getOptionNames());

  }
}
