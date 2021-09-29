package svobodavlad.imagesprocessing.security;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.ResultActions;

import svobodavlad.imagesprocessing.security.User.LoginProvider;
import svobodavlad.imagesprocessing.testutil.SecurityMockUtil;
import svobodavlad.imagesprocessing.testutil.UnitTestTemplate;

class LoginFilterTest extends UnitTestTemplate {
	
	@Autowired
	private PasswordEncoder encoder;

	@MockBean
	private UserRepository userRepository;
	
	@MockBean
	private UserService userService;
	
	@MockBean
	private UserDetailsService userDetailsService;
	
	private User mockedUser;
	
	@BeforeEach
	private void initData() {
		mockedUser = SecurityMockUtil.getMockedDefaultUser();
		mockedUser.setPassword(encoder.encode("pass123"));
		this.given(userDetailsService.loadUserByUsername(mockedUser.getUsername())).willReturn(mockedUser);
		this.given(userRepository.findByUsername(mockedUser.getUsername())).willReturn(Optional.of(mockedUser));
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
				
		this.verify(userService, this.times(1)).updateLastLoginDateTime(mockedUser.getUsername());
	}

	@Test
	void testLoginWrongPasswordUnauthorized401() throws Exception {
		String requestUrl = "/login";
		String requestJson = "{\"username\":\"user1\",\"password\":\"wrongpassword\"}";
		int expectedStatus = 401;
		String expectedJson = "";
		String unexpectedHeader = "Authorization";

		ResultActions mvcResult = this.mockMvcPerformPostNoAuthorization(requestUrl, requestJson);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
		this.mockMvcExpectHeaderDoesNotExist(mvcResult, unexpectedHeader);
		
		this.verify(userService, this.never()).updateLastLoginDateTime(mockedUser.getUsername());
	}

	@Test
	void testLoginInvalidLoginProviderUnauthorized401() throws Exception {
		String requestUrl = "/login";
		String requestJson = "{\"username\":\"user1\",\"password\":\"pass123\"}";
		int expectedStatus = 401;
		String expectedJson = "";
		String unexpectedHeader = "Authorization";
		
		mockedUser.setLoginProvider(LoginProvider.GOOGLE);
		
		ResultActions mvcResult = this.mockMvcPerformPostNoAuthorization(requestUrl, requestJson);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
		this.mockMvcExpectHeaderDoesNotExist(mvcResult, unexpectedHeader);
				
		this.verify(userService, this.never()).updateLastLoginDateTime(mockedUser.getUsername());
	}
	
	@Test
	void testLoginInvalidJsonUnauthorized401() throws Exception {
		String requestUrl = "/login";
		String requestJson = "{\"usernamex\":\"user1\",\"password\":\"pass123\"}";
		int expectedStatus = 401;
		String expectedJson = "";
		String unexpectedHeader = "Authorization";

		ResultActions mvcResult = this.mockMvcPerformPostNoAuthorization(requestUrl, requestJson);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
		this.mockMvcExpectHeaderDoesNotExist(mvcResult, unexpectedHeader);
		
		this.verify(userService, this.never()).updateLastLoginDateTime(mockedUser.getUsername());		
	}

	@Test
	void testLoginUsernameDoesNotExistsUnauthorized401() throws Exception {
		String requestUrl = "/login";
		String requestJson = "{\"username\":\"user1x\",\"password\":\"pass123\"}";
		int expectedStatus = 401;
		String expectedJson = "";
		String unexpectedHeader = "Authorization";
		
		ResultActions mvcResult = this.mockMvcPerformPostNoAuthorization(requestUrl, requestJson);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
		this.mockMvcExpectHeaderDoesNotExist(mvcResult, unexpectedHeader);
		
		this.verify(userService, this.never()).updateLastLoginDateTime("user1x");
	}
}
