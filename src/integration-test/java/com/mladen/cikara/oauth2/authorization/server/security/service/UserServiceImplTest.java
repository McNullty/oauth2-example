package com.mladen.cikara.oauth2.authorization.server.security.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.mladen.cikara.oauth2.authorization.server.security.model.Authority;
import com.mladen.cikara.oauth2.authorization.server.security.model.User;
import com.mladen.cikara.oauth2.authorization.server.security.model.UserDto;
import com.mladen.cikara.oauth2.util.DockerComposeRuleUtil;
import com.palantir.docker.compose.DockerComposeRule;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@DirtiesContext
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceImplTest {

	@ClassRule
	public static DockerComposeRule docker = DockerComposeRuleUtil.getDockerComposeRule();

	@BeforeClass
	public static void setupClass() throws InterruptedException {
		DockerComposeRuleUtil.setDatabaseUrlProperty(docker);
	}

	@Autowired
	private UserService userService;

	@Test
	public void whenSavingUser_thenUserIsAssignedRoleUser() {
		UserDto userDto = new UserDto();
		userDto.setEmail("test@test.com");
		userDto.setFirstName("testFirstName");
		userDto.setLastName("testLastName");
		userDto.setPassword("password");
		userDto.setPasswordConfirmation("password");

		User createdUser = userService.registerUser(userDto);

		assertThat(createdUser.getAuthorities()).contains(Authority.ROLE_USER);
	}
	
	// TODO: Add all the rest of assertions about Registering user

}
