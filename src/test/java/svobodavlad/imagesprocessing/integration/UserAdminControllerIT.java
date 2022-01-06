package svobodavlad.imagesprocessing.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import svobodavlad.imagesprocessing.testutil.IntegTestTemplate;
import svobodavlad.imagesprocessing.testutil.SecurityTestUtil;

public class UserAdminControllerIT extends IntegTestTemplate {

	@Autowired
	private SecurityTestUtil securityTestUtil;
	
	@BeforeEach
	void initData() {
		securityTestUtil.saveAdminUser();
		securityTestUtil.saveDefaultUserInternal();
	}

	@Test
	void testGetAllUsersOk200() throws Exception {
		String requestUrl = "/admin/users";
		int expectedStatus = 200;
		String expectedJson = "[{\"username\":\"user1\",\"givenName\":\"User 1\",\"familyName\":\"User 1\",\"email\":\"user1@gmail.com\",\"lastLoginDateTime\":null,\"previousLoginDateTime\":null,\"userRoles\":[{\"role\":{\"name\":\"ROLE_USER\"}}]},"
				+ "{\"username\":\"admin\",\"givenName\":\"Administrator\",\"familyName\":\"Administrator\",\"email\":\"admin@gmail.com\",\"lastLoginDateTime\":null,\"previousLoginDateTime\":null,\"userRoles\":[{\"role\":{\"name\":\"ROLE_USER\"}},{\"role\":{\"name\":\"ROLE_ADMIN\"}}]}]";		
		ResultActions mvcResult = this.mockMvcPerformGetAuthorizationAdminUser(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}
	
	@Test
	void testUpdateUserOk200() throws Exception {
		String requestUrl = "/admin/user";
		String requestJson = "{\"username\":\"admin\", \"givenName\": \"admin new\",\"familyName\": \"admin new 2\",\"email\": \"adminnew@gmail.com\",\"lastLoginDateTime\":null,\"previousLoginDateTime\":null}";
		int expectedStatus = 200;
		String expectedJson = "{\"username\":\"admin\",\"givenName\":\"admin new\",\"familyName\":\"admin new 2\",\"email\": \"adminnew@gmail.com\",\"lastLoginDateTime\":null,\"previousLoginDateTime\":null,\"userRoles\":[{\"role\":{\"name\":\"ROLE_USER\"}},{\"role\":{\"name\":\"ROLE_ADMIN\"}}]}";

		ResultActions mvcResult = this.mockMvcPerformPutAuthorizationAdminUser(requestUrl, requestJson);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}

	@Test
	void testUpdateUserUsernameDoesNotMatchBadRequest400() throws Exception {
		String requestUrl = "/admin/user";
		String requestJson = "{\"username\":\"adminx\",\"lastLoginDateTime\":null,\"previousLoginDateTime\":null, \"givenName\": \"User X\",\"familyName\": \"User Y\"}";
		int expectedStatus = 400;
		String expectedJson = "";

		ResultActions mvcResult = this.mockMvcPerformPutAuthorizationAdminUser(requestUrl, requestJson);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}	

}
