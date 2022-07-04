package svobodavlad.imagesprocessing.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;

import svobodavlad.imagesprocessing.parameters.ProcessingParametersUserRepository;
import svobodavlad.imagesprocessing.security.UserRepository;
import svobodavlad.imagesprocessing.security.UserTemplate;
import svobodavlad.imagesprocessing.testutil.IntegTestTemplate;
import svobodavlad.imagesprocessing.testutil.SecurityTestUtil;

public class UserControllerIT extends IntegTestTemplate {
	
	private static final String DEFAULT_USERNAME = "user";
	
	private static final String USER_URL = "/user";
	
	private static final int HTTP_OK = 200;
	private static final int HTTP_NO_CONTENT = 204;
	private static final int HTTP_UNAUTHORIZED = 401;	
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ProcessingParametersUserRepository parametersRepository;	
	
	@Autowired
	private SecurityTestUtil securityTestUtil;
	
    @Autowired
    private JacksonTester<UserTemplate> jacksonTester;	
	
	@BeforeEach
	void initData() {
		securityTestUtil.saveDefaultUser();
	}
	
	@Test
	void getUserTemplateOk200() throws Exception {
		var mvcResult = mockMvcPerformGetAuthorizationDefaultUser(USER_URL);
		
		var userTemplate = userRepository.findByUsername(SecurityTestUtil.DEFAULT_USERNAME).get().toUserTemplate();
		var expectedJson = jacksonTester.write(userTemplate).getJson();
		mockMvcExpectStatusAndContent(mvcResult, HTTP_OK, expectedJson);
	}
	
	@Test
	void getUserTemplateOk200NewUser() throws Exception {
		parametersRepository.deleteAll();
		userRepository.deleteAll();
		var mvcResult = mockMvcPerformGetAuthorizationDefaultUser(USER_URL);
		
		var userTemplate = new UserTemplate().setUsername(DEFAULT_USERNAME).setGivenName(DEFAULT_USERNAME).setFamilyName(DEFAULT_USERNAME)
				.setEmail(DEFAULT_USERNAME);
		var expectedJson = jacksonTester.write(userTemplate).getJson();
		mockMvcExpectStatusAndContent(mvcResult, HTTP_OK, expectedJson);
	}	

	@Test
	void getUserTemplateMissingAuthroizationHeaderUnauthorized401() throws Exception {
		var mvcResult = mockMvcPerformGetNoAuthorization(USER_URL);
		mockMvcExpectStatusAndContent(mvcResult, HTTP_UNAUTHORIZED, "");
	}

	@Test	
	void deleteUserNoContent204() throws Exception {
		var mvcResult = mockMvcPerformDeleteAuthorizationDefaultUser(USER_URL);	
		mockMvcExpectStatusAndContent(mvcResult, HTTP_NO_CONTENT, "");
	}

}