package svobodavlad.imagesprocessing.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import svobodavlad.imagesprocessing.security.RoleRepository;
import svobodavlad.imagesprocessing.security.UserRepository;
import svobodavlad.imagesprocessing.testutil.IntegTestTemplate;
import svobodavlad.imagesprocessing.testutil.SecurityTestUtil;

public class UserControllerIT extends IntegTestTemplate {

	@Autowired
	private SecurityTestUtil securityTestUtil;
	
	@Autowired
	private UserRepository userRepository;	
	
	@Autowired
	private RoleRepository roleRepository;
	
	@BeforeEach
	void initData() {
		securityTestUtil.saveAdminUser();
		securityTestUtil.saveDefaultUserInternal();
	}

	@Test
	void testGetCurrentUserOk200() throws Exception {
		String requestUrl = "/user";
		int expectedStatus = 200;
		String expectedJson = "{\"username\":\"user1\",\"givenName\":\"User 1\",\"familyName\":\"User 1\",\"userRoles\":[{\"role\":{\"name\":\"ROLE_USER\"}}],\"lastLoginDateTime\":null,\"previousLoginDateTime\":null}";

		ResultActions mvcResult = this.mockMvcPerformGetAuthorizationDefaultUser(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}

	@Test
	void testGetCurrentUserMissingAuthroizationHeaderNotFound404() throws Exception {
		String requestUrl = "/user";
		int expectedStatus = 404;
		String expectedJson = "";

		ResultActions mvcResult = this.mockMvcPerformGetNoAuthorization(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}

	@Test
	void testGetCurrentUserInvalidAuthroizationHeaderNotFound404() throws Exception {
		String requestUrl = "/user";
		int expectedStatus = 404;
		String expectedJson = "";
		String invalidUsername = "invaliduser";

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
		String requestJson = "{\"username\": \"usernew\", \"password\": \"pass123new\",\"givenName\": \"Test 1\",\"familyName\": \"Test 1\"}";
		int expectedStatus = 201;
		String expectedJson = "";
		String username = "usernew";

		ResultActions mvcResult = this.mockMvcPerformPostNoAuthorization(requestUrl, requestJson);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);

		requestUrl = "/user";
		expectedStatus = 200;
		expectedJson = "{\"username\":\"usernew\",\"givenName\":\"Test 1\",\"familyName\":\"Test 1\",\"userRoles\":[{\"role\":{\"name\":\"ROLE_USER\"}}],\"lastLoginDateTime\":null,\"previousLoginDateTime\":null}";

		mvcResult = this.mockMvcPerformGetAuthorizationForUsername(requestUrl, username);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
		
		requestUrl = "/parameters";
		expectedStatus = 200;
		expectedJson = "{\"timeDiffGroup\":1800,\"resizeWidth\":1000,\"resizeHeight\":1000}";
		
		mvcResult = this.mockMvcPerformGetAuthorizationForUsername(requestUrl, username);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);

	}

	@Test
	void testRegisterUserExistsAlreadyBadRequest400() throws Exception {
		String requestUrl = "/user";
		String requestJson = "{\"username\": \"user1\", \"password\": \"pass123\",\"givenName\": \"User 1\",\"familyName\": \"User 1\"}";
		int expectedStatus = 400;
		String expectedJson = "";

		ResultActions mvcResult = this.mockMvcPerformPostNoAuthorization(requestUrl, requestJson);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}
	
	@Test
	void testRegisterRoleNotFoundBadRequest400() throws Exception {
		String requestUrl = "/user";
		String requestJson = "{\"username\": \"user1\", \"password\": \"pass123\",\"givenName\": \"User 1\",\"familyName\": \"User 1\"}";
		int expectedStatus = 400;
		String expectedJson = "";
		
		userRepository.deleteAll();
		roleRepository.deleteAll();

		ResultActions mvcResult = this.mockMvcPerformPostNoAuthorization(requestUrl, requestJson);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}	

	@Test
	void testUpdateUserOk200() throws Exception {
		String requestUrl = "/user";
		String requestJson = "{\"username\":\"user1\",\"lastLoginDateTime\":null,\"previousLoginDateTime\":null, \"givenName\": \"User X\",\"familyName\": \"User Y\"}";
		int expectedStatus = 200;
		String expectedJson = "{\"username\":\"user1\",\"givenName\":\"User X\",\"familyName\":\"User Y\",\"userRoles\":[{\"role\":{\"name\":\"ROLE_USER\"}}],\"lastLoginDateTime\":null,\"previousLoginDateTime\":null}";

		ResultActions mvcResult = this.mockMvcPerformPutAuthorizationDefaultUser(requestUrl, requestJson);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}

	@Test
	void testUpdateUserUsernameDoesNotMatchBadRequest400() throws Exception {
		String requestUrl = "/user";
		String requestJson = "{\"username\":\"userx\",\"lastLoginDateTime\":null,\"previousLoginDateTime\":null, \"givenName\": \"User X\",\"familyName\": \"User Y\"}";
		int expectedStatus = 400;
		String expectedJson = "";

		ResultActions mvcResult = this.mockMvcPerformPutAuthorizationDefaultUser(requestUrl, requestJson);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}

	@Test
	void testDeleteUserNoContent204() throws Exception {
		String requestUrl = "/user";
		int expectedStatus = 204;
		String expectedJson = "";

		ResultActions mvcResult = this.mockMvcPerformDeleteAuthorizationDefaultUser(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
		
		requestUrl = "/user";
		expectedStatus = 404;
		expectedJson = "";

		mvcResult = this.mockMvcPerformGetAuthorizationDefaultUser(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);		
		
	}

}