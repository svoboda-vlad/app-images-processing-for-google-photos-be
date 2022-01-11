package svobodavlad.imagesprocessing.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import svobodavlad.imagesprocessing.jpaentities.User;
import svobodavlad.imagesprocessing.jpaentities.UserRoles;
import svobodavlad.imagesprocessing.testutil.IntegTestTemplate;
import svobodavlad.imagesprocessing.testutil.SecurityTestUtil;

public class UserAdminControllerIT extends IntegTestTemplate {

	@Autowired
	private SecurityTestUtil securityTestUtil;
	
	private User adminUser;
	private User defaultUser;	
	
	@BeforeEach
	void initData() {
		adminUser = securityTestUtil.saveAdminUser();
		defaultUser = securityTestUtil.saveDefaultUserInternal();
	}

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
	
	@Test
	void testUpdateUserOk200() throws Exception {
		String requestUrl = "/admin/user";
		String requestJson = "{\"username\":\"admin\", \"givenName\": \"admin new\",\"familyName\": \"admin new 2\",\"email\": \"adminnew@gmail.com\",\"lastLoginDateTime\":null,\"previousLoginDateTime\":null}";
		int expectedStatus = 200;
		String rolesJson = "";
		for (UserRoles userRole : adminUser.getRoles()) {
			if (rolesJson.length() > 0) rolesJson += ",";
			rolesJson += "{\"role\":{\"id\":" + userRole.getRole().getId() +",\"name\":\"" + userRole.getRole().getName() + "\"}}";
		}
		String expectedJson = "{\"username\":\"admin\",\"givenName\":\"admin new\",\"familyName\":\"admin new 2\",\"email\": \"adminnew@gmail.com\",\"lastLoginDateTime\":null,\"previousLoginDateTime\":null,\"userRoles\":[" + rolesJson + "]}";

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
