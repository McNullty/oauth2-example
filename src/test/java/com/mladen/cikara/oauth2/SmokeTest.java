package com.mladen.cikara.oauth2;

import static org.assertj.core.api.Assertions.assertThat;

import com.mladen.cikara.oauth2.resource.server.controller.TestController;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SmokeTest {

  @Autowired
  private TestController testController;

  @Test
  public void test() {
    assertThat(this.testController).isNotNull();
  }

}
