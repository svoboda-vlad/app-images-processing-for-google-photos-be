package svobodavlad.imagesprocessing.integration;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import svobodavlad.imagesprocessing.jpaentities.User;
import svobodavlad.imagesprocessing.jpaentities.UserRoles;
import svobodavlad.imagesprocessing.testutil.IntegTestTemplate;

public class UserAdminControllerIT extends IntegTestTemplate {
	
	private User adminUser;
	private User defaultUser;

	@Test
	void testGetAllUsersOk200() throws Exception {
		String requestUrl = "/admin/users";
		int expectedStatus = 200;
		String rolesJsonAdmin = "";
		for (UserRoles userRole : adminUser.getRoles()) {
			if (rolesJsonAdmin.length() > 0) rolesJsonAdmin += ",";
			rolesJsonAdmin += "{\"role\":{\"id\":" + userRole.getRole().getId() +",\"name\":\"" + userRole.getRole().getName() + "\"}}";
		}
		String rolesJsonDefault = "";
		for (UserRoles userRole : defaultUser.getRoles()) {
			if (rolesJsonDefault.length() > 0) rolesJsonDefault += ",";
			rolesJsonDefault += "{\"role\":{\"id\":" + userRole.getRole().getId() +",\"name\":\"" + userRole.getRole().getName() + "\"}}";
		}		
		String expectedJson = "[{\"username\":\"admin\",\"givenName\":\"Administrator\",\"familyName\":\"Administrator\",\"email\":\"admin@gmail.com\",\"lastLoginDateTime\":null,\"previousLoginDateTime\":null,\"userRoles\":[" 
		+ rolesJsonAdmin 
		+ "]},"
		+ "{\"username\":\"user1\",\"givenName\":\"User 1\",\"familyName\":\"User 1\",\"email\":\"user1@gmail.com\",\"lastLoginDateTime\":null,\"previousLoginDateTime\":null,\"userRoles\":[" 
		+ rolesJsonDefault 
		+ "]}]";
		ResultActions mvcResult = this.mockMvcPerformGetAuthorizationAdminUser(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}

}
