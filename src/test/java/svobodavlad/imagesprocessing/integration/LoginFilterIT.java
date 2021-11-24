package svobodavlad.imagesprocessing.integration;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.ResultActions;

import svobodavlad.imagesprocessing.jpaentities.User;
import svobodavlad.imagesprocessing.jpaentities.User.LoginProvider;
import svobodavlad.imagesprocessing.security.UserRepository;
import svobodavlad.imagesprocessing.testutil.IntegTestTemplate;
import svobodavlad.imagesprocessing.testutil.SecurityTestUtil;

class LoginFilterIT extends IntegTestTemplate {

	@Autowired
	private PasswordEncoder encoder;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private SecurityTestUtil securityTestUtil;
	
	private User defaultUser;

	@BeforeEach
	void initData() {
		defaultUser = securityTestUtil.saveDefaultUserInternal();
	}

	@Test
	void testLoginNoLastLoginDateTimeOk200() throws Exception {
		String requestUrl = "/login";
		String requestJson = "{\"username\":\"user1\",\"password\":\"pass123\"}";
		int expectedStatus = 200;
		String expectedJson = "";
		String expectedHeader = "Authorization";
		
		ResultActions mvcResult = this.mockMvcPerformPostNoAuthorization(requestUrl, requestJson);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
		this.mockMvcExpectHeaderExists(mvcResult, expectedHeader);

		Optional<User> user = userRepository.findByUsername(defaultUser.getUsername());

		this.assertThat(user.get().getLastLoginDateTime()).isNotNull();
		this.assertThat(user.get().getPreviousLoginDateTime()).isNotNull();
	}

	@Test
	void testLoginWithLastLoginDateTimeOk200() throws Exception {
		String requestUrl = "/login";
		String requestJson = "{\"username\":\"user322\",\"password\":\"pass322\"}";
		int expectedStatus = 200;
		String expectedJson = "";
		String expectedHeader = "Authorization";

		User userWithLastLogin = new User("user322", encoder.encode("pass322"), LoginProvider.INTERNAL, "User 322",
				"User 322");
		LocalDateTime lastLoginDateTime = LocalDateTime.now();
		userWithLastLogin.setLastLoginDateTime(lastLoginDateTime);
		userRepository.save(userWithLastLogin);

		ResultActions mvcResult = this.mockMvcPerformPostNoAuthorization(requestUrl, requestJson);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
		this.mockMvcExpectHeaderExists(mvcResult, expectedHeader);

		Optional<User> user = userRepository.findByUsername("user322");

		this.assertThat(user.get().getLastLoginDateTime()).isAfter(lastLoginDateTime);
		this.assertThat(user.get().getPreviousLoginDateTime()).isEqualTo(lastLoginDateTime);

	}

	@Test
	void testLoginWrongPasswordUnauthorized401() throws Exception {
		String requestUrl = "/login";
		String requestJson = "{\"username\":\"user321\",\"password\":\"wrongpassword\"}";
		int expectedStatus = 401;
		String expectedJson = "";
		String unexpectedHeader = "Authorization";

		ResultActions mvcResult = this.mockMvcPerformPostNoAuthorization(requestUrl, requestJson);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
		this.mockMvcExpectHeaderDoesNotExist(mvcResult, unexpectedHeader);
	}

	@Test
	void testLoginInvalidLoginProviderUnauthorized401() throws Exception {
		String requestUrl = "/login";
		String requestJson = "{\"username\":\"user323\",\"password\":\"pass323\"}";
		int expectedStatus = 401;
		String expectedJson = "";
		String unexpectedHeader = "Authorization";

		User userWithGoogleLogin = new User("user323", encoder.encode("pass323"), LoginProvider.GOOGLE, "User 323",
				"User 323");
		LocalDateTime lastLoginDateTime = LocalDateTime.now();
		userWithGoogleLogin.setLastLoginDateTime(lastLoginDateTime);
		userRepository.save(userWithGoogleLogin);

		ResultActions mvcResult = this.mockMvcPerformPostNoAuthorization(requestUrl, requestJson);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
		this.mockMvcExpectHeaderDoesNotExist(mvcResult, unexpectedHeader);
	}

	@Test
	void testLoginInvalidJsonUnauthorized401() throws Exception {
		String requestUrl = "/login";
		String requestJson = "{\"usernamex\":\"user321\",\"password\":\"pass321\"}";
		int expectedStatus = 401;
		String expectedJson = "";
		String unexpectedHeader = "Authorization";

		ResultActions mvcResult = this.mockMvcPerformPostNoAuthorization(requestUrl, requestJson);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
		this.mockMvcExpectHeaderDoesNotExist(mvcResult, unexpectedHeader);
	}

	@Test
	void testLoginUsernameDoesNotExistsUnauthorized401() throws Exception {
		String requestUrl = "/login";
		String requestJson = "{\"username\":\"user321x\",\"password\":\"pass321\"}";
		int expectedStatus = 401;
		String expectedJson = "";
		String unexpectedHeader = "Authorization";

		ResultActions mvcResult = this.mockMvcPerformPostNoAuthorization(requestUrl, requestJson);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
		this.mockMvcExpectHeaderDoesNotExist(mvcResult, unexpectedHeader);
	}

}