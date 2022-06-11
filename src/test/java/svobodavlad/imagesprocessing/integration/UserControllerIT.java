package svobodavlad.imagesprocessing.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import svobodavlad.imagesprocessing.jpaentities.User;
import svobodavlad.imagesprocessing.jpaentities.UserRoles;
import svobodavlad.imagesprocessing.parameters.ProcessingParametersUserRepository;
import svobodavlad.imagesprocessing.security.UserRepository;
import svobodavlad.imagesprocessing.testutil.IntegTestTemplate;
import svobodavlad.imagesprocessing.testutil.SecurityTestUtil;

public class UserControllerIT extends IntegTestTemplate {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ProcessingParametersUserRepository parametersRepository;	
	
	@Autowired
	private SecurityTestUtil securityTestUtil;
	
	@BeforeEach
	void initData() {
		securityTestUtil.saveDefaultUser();
	}
	
	@Test
	void getUserInfoOk200() throws Exception {
		User defaultUser = userRepository.findByUsername(SecurityTestUtil.DEFAULT_USERNAME).get();
		
		String requestUrl = "/user";
		int expectedStatus = 200;
		String rolesJson = "";
		for (UserRoles userRole : defaultUser.getRoles()) {
			if (rolesJson.length() > 0) rolesJson += ",";
			rolesJson += "{\"role\":{\"id\":" + userRole.getRole().getId() +",\"name\":\"" + userRole.getRole().getName() + "\"}}";
		}

		ResultActions mvcResult = this.mockMvcPerformGetAuthorizationDefaultUser(requestUrl);
		
		defaultUser = userRepository.getById(defaultUser.getId());
		String expectedJson = "{\"username\":\"user\",\"givenName\":\"user\",\"familyName\":\"user\",\"email\":\"user\",\"userRoles\":[" 
				+ rolesJson + "],\"lastLoginDateTime\":\"" 
				+ defaultUser.getLastLoginDateTime() + "\",\"previousLoginDateTime\":\"" 
				+ defaultUser.getPreviousLoginDateTime() + "\"}";

		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}
	
	@Test
	void getUserInfoOk200NewUser() throws Exception {
		parametersRepository.deleteAll();
		userRepository.deleteAll();
		
		String requestUrl = "/user";
		int expectedStatus = 200;

		ResultActions mvcResult = this.mockMvcPerformGetAuthorizationDefaultUser(requestUrl);
		
		User defaultUser = userRepository.findByUsername(SecurityTestUtil.DEFAULT_USERNAME).get();
		String rolesJson = "";
		for (UserRoles userRole : defaultUser.getRoles()) {
			if (rolesJson.length() > 0) rolesJson += ",";
			rolesJson += "{\"role\":{\"id\":" + userRole.getRole().getId() +",\"name\":\"" + userRole.getRole().getName() + "\"}}";
		}
		String expectedJson = "{\"username\":\"user\",\"givenName\":\"user\",\"familyName\":\"user\",\"email\":\"user\",\"userRoles\":[" 
				+ rolesJson + "],\"lastLoginDateTime\":\"" 
				+ defaultUser.getLastLoginDateTime() + "\",\"previousLoginDateTime\":\"" 
				+ defaultUser.getPreviousLoginDateTime() + "\"}";

		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}	

	@Test
	void getUserInfoMissingAuthroizationHeaderUnauthorized401() throws Exception {
		String requestUrl = "/user";
		int expectedStatus = 401;
		String expectedJson = "";

		ResultActions mvcResult = this.mockMvcPerformGetNoAuthorization(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}

	@Test	
	void deleteUserNoContent204() throws Exception {
		String requestUrl = "/user";
		int expectedStatus = 204;
		String expectedJson = "";

		ResultActions mvcResult = this.mockMvcPerformDeleteAuthorizationDefaultUser(requestUrl);	
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}

}