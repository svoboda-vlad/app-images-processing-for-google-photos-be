package svobodavlad.imagesprocessing.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import svobodavlad.imagesprocessing.jpaentities.User;
import svobodavlad.imagesprocessing.parameters.ProcessingParametersDefaultRepository;
import svobodavlad.imagesprocessing.parameters.ProcessingParametersUserRepository;
import svobodavlad.imagesprocessing.parameters.ProcessingParametersUserService;
import svobodavlad.imagesprocessing.testutil.IntegTestTemplate;
import svobodavlad.imagesprocessing.testutil.SecurityTestUtil;

public class ProcessingParametersUserControllerIT extends IntegTestTemplate {
	
	@Autowired
	private ProcessingParametersUserRepository parametersRepository;
	
	@Autowired
	private ProcessingParametersDefaultRepository parametersDefaultRepository;
	
	@Autowired
	private SecurityTestUtil securityTestUtil;
	
	@Autowired
	private ProcessingParametersUserService parametersService;
	
	@BeforeEach
	void initData() {
		User defaultUser = securityTestUtil.saveDefaultUser();
		parametersService.setInitialParameters(defaultUser.getUsername());
	}
		
	@Test
	void getProcessingParametersUserTemplateOk200() throws Exception {		
		String requestUrl = "/parameters";
		int expectedStatus = 200;
		String expectedJson = "{\"timeDiffGroup\":1800,\"resizeWidth\":1000,\"resizeHeight\":1000}";
		
		ResultActions mvcResult = this.mockMvcPerformGetAuthorizationDefaultUser(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}
	
	@Test
	void getProcessingParametersUserTemplateNotFound404() throws Exception {
		String requestUrl = "/parameters";
		int expectedStatus = 404;
		String expectedJson = "";
		
		parametersRepository.deleteAll();

		ResultActions mvcResult = this.mockMvcPerformGetAuthorizationDefaultUser(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);	
	}	

	@Test
	void updateProcessingParametersUserTemplateOk200() throws Exception {
		String requestUrl = "/parameters";
		String requestJson = "{\"timeDiffGroup\":7200,\"resizeWidth\":1000,\"resizeHeight\":1000}";
		int expectedStatus = 200;
		String expectedJson = "{\"timeDiffGroup\":7200,\"resizeWidth\":1000,\"resizeHeight\":1000}";

		ResultActions mvcResult = this.mockMvcPerformPutAuthorizationDefaultUser(requestUrl, requestJson);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}
	
	@Test
	void updateProcessingParametersUserTemplateNotFound404() throws Exception {
		String requestUrl = "/parameters";
		String requestJson = "{\"timeDiffGroup\":3600,\"resizeWidth\":1000,\"resizeHeight\":1000}";
		int expectedStatus = 404;
		String expectedJson = "";
		
		parametersRepository.deleteAll();

		ResultActions mvcResult = this.mockMvcPerformPutAuthorizationDefaultUser(requestUrl, requestJson);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}
	
	@Test
	void getResetToDefaultOk204() throws Exception {
		String requestUrl = "/parameters-reset-to-default";
		int expectedStatus = 204;
		String expectedJson = "";
				
		ResultActions mvcResult = this.mockMvcPerformGetAuthorizationDefaultUser(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
		
		requestUrl = "/parameters";
		expectedStatus = 200;
		expectedJson = "{\"timeDiffGroup\":1800,\"resizeWidth\":1000,\"resizeHeight\":1000}";

		mvcResult = this.mockMvcPerformGetAuthorizationDefaultUser(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}
		
	@Test
	void getResetToDefaultNoUserParametersOk204() throws Exception {
		String requestUrl = "/parameters-reset-to-default";
		int expectedStatus = 204;
		String expectedJson = "";
		
		parametersRepository.deleteAll();
				
		ResultActions mvcResult = this.mockMvcPerformGetAuthorizationDefaultUser(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
		
		requestUrl = "/parameters";
		expectedStatus = 200;
		expectedJson = "{\"timeDiffGroup\":1800,\"resizeWidth\":1000,\"resizeHeight\":1000}";

		mvcResult = this.mockMvcPerformGetAuthorizationDefaultUser(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);	
	}
	
	@Test
	void getResetToDefaultNoDefaultParametersNotFound404() throws Exception {
		String requestUrl = "/parameters-reset-to-default";
		int expectedStatus = 404;
		String expectedJson = "";
		
		parametersDefaultRepository.deleteAll();
		
		ResultActions mvcResult = this.mockMvcPerformGetAuthorizationDefaultUser(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}	
	
}