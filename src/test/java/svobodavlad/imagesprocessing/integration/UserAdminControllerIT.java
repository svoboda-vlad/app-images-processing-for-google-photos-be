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
		securityTestUtil.saveDefaultUser();
	}

	@Test
	void testGetAllUsersOk200() throws Exception {
		String requestUrl = "/admin/users";
		int expectedStatus = 200;
		String expectedJson = "[{\"username\":\"user1\",\"givenName\":\"User 1\",\"familyName\":\"User 1\",\"lastLoginDateTime\":null,\"previousLoginDateTime\":null,\"userRoles\":[{\"role\":{\"name\":\"ROLE_USER\"}}]},"
				+ "{\"username\":\"admin\",\"givenName\":\"Administrator\",\"familyName\":\"Administrator\",\"lastLoginDateTime\":null,\"previousLoginDateTime\":null,\"userRoles\":[{\"role\":{\"name\":\"ROLE_USER\"}},{\"role\":{\"name\":\"ROLE_ADMIN\"}}]}]";
		
		ResultActions mvcResult = this.mockMvcPerformGetAuthorizationAdminUser(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}

}
