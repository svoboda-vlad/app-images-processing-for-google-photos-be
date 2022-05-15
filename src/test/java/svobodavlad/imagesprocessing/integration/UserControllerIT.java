package svobodavlad.imagesprocessing.integration;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import svobodavlad.imagesprocessing.jpaentities.User;
import svobodavlad.imagesprocessing.jpaentities.UserRoles;
import svobodavlad.imagesprocessing.testutil.IntegTestTemplate;

public class UserControllerIT extends IntegTestTemplate {

	private User defaultUser;
	
	@Test
	void testGetCurrentUserOk200() throws Exception {
		String requestUrl = "/user";
		int expectedStatus = 200;
		String rolesJson = "";
		for (UserRoles userRole : defaultUser.getRoles()) {
			if (rolesJson.length() > 0) rolesJson += ",";
			rolesJson += "{\"role\":{\"id\":" + userRole.getRole().getId() +",\"name\":\"" + userRole.getRole().getName() + "\"}}";
		}		
		String expectedJson = "{\"username\":\"user1\",\"givenName\":\"User 1\",\"familyName\":\"User 1\",\"email\":\"user1@gmail.com\",\"userRoles\":[" + rolesJson + "],\"lastLoginDateTime\":null,\"previousLoginDateTime\":null}";

		ResultActions mvcResult = this.mockMvcPerformGetAuthorizationDefaultUser(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}

	@Test
	void testGetCurrentUserMissingAuthroizationHeaderForbidden403() throws Exception {
		String requestUrl = "/user";
		int expectedStatus = 403;
		String expectedJson = "";

		ResultActions mvcResult = this.mockMvcPerformGetNoAuthorization(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}

	@Test	
	void testDeleteUserNoContent204() throws Exception {
		String requestUrl = "/user";
		int expectedStatus = 204;
		String expectedJson = "";

		ResultActions mvcResult = this.mockMvcPerformDeleteAuthorizationDefaultUser(requestUrl);	
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}

}