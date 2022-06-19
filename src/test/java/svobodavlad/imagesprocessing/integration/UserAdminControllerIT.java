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
		securityTestUtil.saveDefaultUser();
	}	

	@Test
	void getAllUsersOk200() throws Exception {
		String requestUrl = "/admin/users";
		int expectedStatus = 200;
		String expectedJson = "[{\"username\":\"user\",\"givenName\":\"user\",\"familyName\":\"user\",\"email\":null,\"lastLoginDateTime\":null,\"previousLoginDateTime\":null}]";
		ResultActions mvcResult = this.mockMvcPerformGetAuthorizationAdminUser(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}

}
