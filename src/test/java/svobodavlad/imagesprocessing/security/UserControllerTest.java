package svobodavlad.imagesprocessing.security;

import java.util.ArrayList;
import java.util.Optional;

import javax.persistence.EntityExistsException;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.ResultActions;

import svobodavlad.imagesprocessing.testutil.SecurityMockUtil;
import svobodavlad.imagesprocessing.testutil.UnitTestTemplate;

class UserControllerTest extends UnitTestTemplate {

	@MockBean
	private UserRepository userRepository;

	@MockBean
	private UserService userService;

	@MockBean
	private UserDetailsService userDetailsService;

	@MockBean
	private PasswordEncoder encoder;

	private static final String USERNAME_NEW = "usernew";
	private static final String PASSWORD_NEW = "pass123new";

	@Test
	void testGetCurrentUserOk200() throws Exception {
		String requestUrl = "/user";
		int expectedStatus = 200;
		String expectedJson = "{\"username\":\"user1\",\"givenName\":\"User 1\",\"familyName\":\"User 1\",\"userRoles\":[{\"role\":{\"id\":0,\"name\":\"ROLE_USER\"}}],\"lastLoginDateTime\":null,\"previousLoginDateTime\":null}";

		this.given(userDetailsService.loadUserByUsername(SecurityMockUtil.getMockedDefaultUser().getUsername())).willReturn(SecurityMockUtil.getMockedDefaultUser());
		this.given(userRepository.findByUsername(SecurityMockUtil.getMockedDefaultUser().getUsername())).willReturn(Optional.of(SecurityMockUtil.getMockedDefaultUser()));

		ResultActions mvcResult = this.mockMvcPerformGetAuthorizationDefaultUser(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}

	@Test
	void testGetCurrentUserMissingAuthorizationHeaderNotFound404() throws Exception {
		String requestUrl = "/user";
		int expectedStatus = 404;
		String expectedJson = "";
		
		ResultActions mvcResult = this.mockMvcPerformGetNoAuthorization(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}

	@Test
	void testGetCurrentUserInvalidAuthorizationHeaderNotFound404() throws Exception {
		String requestUrl = "/user";
		int expectedStatus = 404;
		String expectedJson = "";
		String invalidUsername = "userinvalid";

		this.given(userDetailsService.loadUserByUsername(invalidUsername))
				.willThrow(new UsernameNotFoundException("User not found."));
		
		ResultActions mvcResult = this.mockMvcPerformGetAuthorizationForUsername(requestUrl, invalidUsername);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}

	@Test
	void testGetCurrentUserInvalidTokenNotFound404() throws Exception {
		String requestUrl = "/user";
		int expectedStatus = 404;
		String expectedJson = "";
		
		ResultActions mvcResult = this.mockMvcPerformGetAuthorizationInvalidToken(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}

	@Test
	void testRegisterUserCreated201() throws Exception {
		String requestUrl = "/user";
		String requestJson = "{\"username\":\"usernew\",\"password\":\"pass123new\",\"givenName\":\"New User\",\"familyName\":\"New User\"}";
		int expectedStatus = 201;
		String expectedJson = "";

		this.given(encoder.encode(PASSWORD_NEW)).willReturn("A".repeat(60));
		UserRegister userRegister = new UserRegister(USERNAME_NEW, PASSWORD_NEW, "New User", "New User");
		User user = userRegister.toUserInternal(encoder);

		ResultActions mvcResult = this.mockMvcPerformPostNoAuthorization(requestUrl, requestJson);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);

		this.verify(userService, this.times(1)).registerUser(user);
	}

	@Test
	void testRegisterUserExistsAlreadyBadRequest400() throws Exception {
		String requestUrl = "/user";
		String requestJson = "{\"username\": \"usernew\", \"password\": \"pass123new\", \"givenName\": \"New User\",\"familyName\": \"New User\"}";
		int expectedStatus = 400;
		String expectedJson = "";

		this.given(encoder.encode(PASSWORD_NEW)).willReturn("A".repeat(60));
		UserRegister userRegister = new UserRegister(USERNAME_NEW, PASSWORD_NEW, "New User", "New User");
		User user = userRegister.toUserInternal(encoder);

		this.given(userService.registerUser(user)).willThrow(new EntityExistsException("User already exists."));
		
		ResultActions mvcResult = this.mockMvcPerformPostNoAuthorization(requestUrl, requestJson);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}

	@Test
	void testUpdateUserOk200() throws Exception {
		String requestUrl = "/user";
		String requestJson = "{\"username\":\"user1\",\"lastLoginDateTime\":null,\"previousLoginDateTime\":null, \"givenName\": \"User X\",\"familyName\": \"User Y\"}";
		int expectedStatus = 200;
		String expectedJson = "{\"username\":\"user1\",\"givenName\":\"User X\",\"familyName\":\"User Y\",\"userRoles\":[{\"role\":{\"id\":0,\"name\":\"ROLE_USER\"}}],\"lastLoginDateTime\":null,\"previousLoginDateTime\":null}";

		this.given(userDetailsService.loadUserByUsername(SecurityMockUtil.getMockedDefaultUser().getUsername())).willReturn(SecurityMockUtil.getMockedDefaultUser());

		UserInfo userInfo = new UserInfo(SecurityMockUtil.getMockedDefaultUser().getUsername(), "User X", "User Y", null, null, new ArrayList<UserRoles>());

		this.given(userService.updateUser(userInfo)).willReturn(userInfo.toUser(SecurityMockUtil.getMockedDefaultUser()));
		
		ResultActions mvcResult = this.mockMvcPerformPutAuthorizationDefaultUser(requestUrl, requestJson);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}

	@Test
	void testUpdateUserUsernameDoesNotMatchBadRequest400() throws Exception {
		String requestUrl = "/user";
		String requestJson = "{\"username\":\"userx\",\"lastLoginDateTime\":null,\"previousLoginDateTime\":null, \"givenName\": \"User X\",\"familyName\": \"User Y\"}";
		int expectedStatus = 400;
		String expectedJson = "";

		this.given(userDetailsService.loadUserByUsername(SecurityMockUtil.getMockedDefaultUser().getUsername())).willReturn(SecurityMockUtil.getMockedDefaultUser());

		ResultActions mvcResult = this.mockMvcPerformPutAuthorizationDefaultUser(requestUrl, requestJson);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}

	@Test
	void testDeleteUserNoContent204() throws Exception {
		String requestUrl = "/user";
		int expectedStatus = 204;
		String expectedJson = "";

		User user = SecurityMockUtil.getMockedDefaultUser();
		user.setId(1L);

		this.given(userDetailsService.loadUserByUsername(user.getUsername())).willReturn(user);
		this.given(userRepository.findByUsername(user.getUsername())).willReturn(Optional.of(user));
		
		ResultActions mvcResult = this.mockMvcPerformDeleteAuthorizationDefaultUser(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);

		this.verify(userRepository, this.times(1)).deleteById(1L);
	}

}
