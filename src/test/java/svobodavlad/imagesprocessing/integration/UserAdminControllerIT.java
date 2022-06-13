package svobodavlad.imagesprocessing.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import svobodavlad.imagesprocessing.jpaentities.User;
import svobodavlad.imagesprocessing.jpaentities.UserRoles;
import svobodavlad.imagesprocessing.security.UserRepository;
import svobodavlad.imagesprocessing.testutil.IntegTestTemplate;
import svobodavlad.imagesprocessing.testutil.SecurityTestUtil;

public class UserAdminControllerIT extends IntegTestTemplate {
	
	private final static String ADMIN_USERNAME = "admin";
	
	@Autowired
	private UserRepository userRepository;	
	
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
		String rolesJsonAdmin = "";
		User adminUser = userRepository.findByUsername(ADMIN_USERNAME).get();
		for (UserRoles userRole : adminUser.getRoles()) {
			if (rolesJsonAdmin.length() > 0) rolesJsonAdmin += ",";
			rolesJsonAdmin += "{\"role\":{\"id\":" + userRole.getRole().getId() +",\"name\":\"" + userRole.getRole().getName() + "\"}}";
		}
		String rolesJsonDefault = "";
		User defaultUser = userRepository.findByUsername(SecurityTestUtil.DEFAULT_USERNAME).get();
		for (UserRoles userRole : defaultUser.getRoles()) {
			if (rolesJsonDefault.length() > 0) rolesJsonDefault += ",";
			rolesJsonDefault += "{\"role\":{\"id\":" + userRole.getRole().getId() +",\"name\":\"" + userRole.getRole().getName() + "\"}}";
		}		
		String expectedJson = "[{\"username\":\"admin\",\"givenName\":\"admin\",\"familyName\":\"admin\",\"email\":null,\"lastLoginDateTime\":null,\"previousLoginDateTime\":null,\"userRoles\":[" 
		+ rolesJsonAdmin 
		+ "]},"
		+ "{\"username\":\"user\",\"givenName\":\"user\",\"familyName\":\"user\",\"email\":null,\"lastLoginDateTime\":null,\"previousLoginDateTime\":null,\"userRoles\":[" 
		+ rolesJsonDefault 
		+ "]}]";
		ResultActions mvcResult = this.mockMvcPerformGetAuthorizationAdminUser(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}

}
