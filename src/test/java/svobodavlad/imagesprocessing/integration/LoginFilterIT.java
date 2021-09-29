package svobodavlad.imagesprocessing.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import svobodavlad.imagesprocessing.security.User;
import svobodavlad.imagesprocessing.security.User.LoginProvider;
import svobodavlad.imagesprocessing.security.UserRepository;
import svobodavlad.imagesprocessing.security.UserRoles;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("liquibase")
//@WithMockUser - not needed
class LoginFilterIT {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private PasswordEncoder encoder;

	@Autowired
	private UserRepository userRepository;

	@BeforeEach
	void initData() {
		User user = new User(0L, "user321", encoder.encode("pass321"), LoginProvider.INTERNAL, "User 321", "User 321", null, null, new ArrayList<UserRoles>());
		userRepository.save(user);
	}

	@Test
	void testLoginNoLastLoginDateTimeOk200() throws Exception {
		String requestUrl = "/login";
		String requestJson = "{\"username\":\"user321\",\"password\":\"pass321\"}";
		int expectedStatus = 200;
		String expectedJson = "";

		this.mvc.perform(post(requestUrl).content(requestJson).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is(expectedStatus)).andExpect(content().string(expectedJson))
				.andExpect(header().exists("Authorization"));

		Optional<User> user = userRepository.findByUsername("user321");

		assertThat(user.get().getLastLoginDateTime()).isNotNull();
		assertThat(user.get().getPreviousLoginDateTime()).isNotNull();
	}

	@Test
	void testLoginWithLastLoginDateTimeOk200() throws Exception {
		String requestUrl = "/login";
		String requestJson = "{\"username\":\"user322\",\"password\":\"pass322\"}";
		int expectedStatus = 200;
		String expectedJson = "";

		User userWithLastLogin = new User(0L, "user322", encoder.encode("pass322"), LoginProvider.INTERNAL, "User 322",
				"User 322", null, null, new ArrayList<UserRoles>());
		LocalDateTime lastLoginDateTime = LocalDateTime.now();
		userWithLastLogin.setLastLoginDateTime(lastLoginDateTime);
		userRepository.save(userWithLastLogin);

		this.mvc.perform(post(requestUrl).content(requestJson).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is(expectedStatus)).andExpect(content().string(expectedJson))
				.andExpect(header().exists("Authorization"));

		Optional<User> user = userRepository.findByUsername("user322");

		assertThat(user.get().getLastLoginDateTime()).isAfter(lastLoginDateTime);
		assertThat(user.get().getPreviousLoginDateTime()).isEqualTo(lastLoginDateTime);

	}

	@Test
	void testLoginWrongPasswordUnauthorized401() throws Exception {
		String requestUrl = "/login";
		String requestJson = "{\"username\":\"user321\",\"password\":\"wrongpassword\"}";
		int expectedStatus = 401;
		String expectedJson = "";

		this.mvc.perform(post(requestUrl).content(requestJson).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is(expectedStatus)).andExpect(content().string(expectedJson))
				.andExpect(header().doesNotExist("Authorization"));
	}

	@Test
	void testLoginInvalidLoginProviderUnauthorized401() throws Exception {
		String requestUrl = "/login";
		String requestJson = "{\"username\":\"user323\",\"password\":\"pass323\"}";
		int expectedStatus = 401;
		String expectedJson = "";

		User userWithGoogleLogin = new User(0L, "user323", encoder.encode("pass323"), LoginProvider.GOOGLE, "User 323",
				"User 323", null, null, new ArrayList<UserRoles>());
		LocalDateTime lastLoginDateTime = LocalDateTime.now();
		userWithGoogleLogin.setLastLoginDateTime(lastLoginDateTime);
		userRepository.save(userWithGoogleLogin);

		this.mvc.perform(post(requestUrl).content(requestJson).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is(expectedStatus)).andExpect(content().string(expectedJson))
				.andExpect(header().doesNotExist("Authorization"));
	}

	@Test
	void testLoginInvalidJsonUnauthorized401() throws Exception {
		String requestUrl = "/login";
		String requestJson = "{\"usernamex\":\"user321\",\"password\":\"pass321\"}";
		int expectedStatus = 401;
		String expectedJson = "";

		this.mvc.perform(post(requestUrl).content(requestJson).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is(expectedStatus)).andExpect(content().string(expectedJson))
				.andExpect(header().doesNotExist("Authorization"));
	}

	@Test
	void testLoginUsernameDoesNotExistsUnauthorized401() throws Exception {
		String requestUrl = "/login";
		String requestJson = "{\"username\":\"user321x\",\"password\":\"pass321\"}";
		int expectedStatus = 401;
		String expectedJson = "";

		this.mvc.perform(post(requestUrl).content(requestJson).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is(expectedStatus)).andExpect(content().string(expectedJson))
				.andExpect(header().doesNotExist("Authorization"));
	}

}