package svobodavlad.imagesprocessing.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import svobodavlad.imagesprocessing.parameters.ProcessingParametersDefaultRepository;
import svobodavlad.imagesprocessing.parameters.ProcessingParametersUserRepository;
import svobodavlad.imagesprocessing.testutil.IntegTestTemplate;
import svobodavlad.imagesprocessing.testutil.SecurityTestUtil;

public class ProcessingParametersUserControllerIT extends IntegTestTemplate {
	
	@Autowired
	private SecurityTestUtil securityTestUtil;
	
	@Autowired
	private ProcessingParametersUserRepository parametersRepository;
	
	@Autowired
	private ProcessingParametersDefaultRepository parametersDefaultRepository;	
		
	@BeforeEach
	void initData() {
		securityTestUtil.saveDefaultUserInternal();
	}

	@Test
	void testGetProcessingParametersUserTemplateOk200() throws Exception {		
		String requestUrl = "/parameters";
		int expectedStatus = 200;
		String expectedJson = "{\"timeDiffGroup\":1800,\"resizeWidth\":1000,\"resizeHeight\":1000}";
		
		ResultActions mvcResult = this.mockMvcPerformGetAuthorizationDefaultUserInternal(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}
	
	@Test
	void testGetProcessingParametersUserTemplateNotFound404() throws Exception {
		String requestUrl = "/parameters";
		int expectedStatus = 404;
		String expectedJson = "";
		
		parametersRepository.deleteAll();

		ResultActions mvcResult = this.mockMvcPerformGetAuthorizationDefaultUserInternal(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);	
	}	

	@Test
	void testUpdateProcessingParametersUserTemplateOk200() throws Exception {
		String requestUrl = "/parameters";
		String requestJson = "{\"timeDiffGroup\":7200,\"resizeWidth\":1000,\"resizeHeight\":1000}";
		int expectedStatus = 200;
		String expectedJson = "{\"timeDiffGroup\":7200,\"resizeWidth\":1000,\"resizeHeight\":1000}";

		ResultActions mvcResult = this.mockMvcPerformPutAuthorizationDefaultUserInternal(requestUrl, requestJson);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}
	
	@Test
	void testUpdateProcessingParametersUserTemplateNotFound404() throws Exception {
		String requestUrl = "/parameters";
		String requestJson = "{\"timeDiffGroup\":3600,\"resizeWidth\":1000,\"resizeHeight\":1000}";
		int expectedStatus = 404;
		String expectedJson = "";
		
		parametersRepository.deleteAll();

		ResultActions mvcResult = this.mockMvcPerformPutAuthorizationDefaultUserInternal(requestUrl, requestJson);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}
	
	@Test
	void testGetResetToDefaultOk204() throws Exception {
		String requestUrl = "/parameters-reset-to-default";
		int expectedStatus = 204;
		String expectedJson = "";
				
		ResultActions mvcResult = this.mockMvcPerformGetAuthorizationDefaultUserInternal(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
		
		requestUrl = "/parameters";
		expectedStatus = 200;
		expectedJson = "{\"timeDiffGroup\":1800,\"resizeWidth\":1000,\"resizeHeight\":1000}";

		mvcResult = this.mockMvcPerformGetAuthorizationDefaultUserInternal(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}
		
	@Test
	void testGetResetToDefaultNoUserParametersOk204() throws Exception {
		String requestUrl = "/parameters-reset-to-default";
		int expectedStatus = 204;
		String expectedJson = "";
		
		parametersRepository.deleteAll();
				
		ResultActions mvcResult = this.mockMvcPerformGetAuthorizationDefaultUserInternal(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
		
		requestUrl = "/parameters";
		expectedStatus = 200;
		expectedJson = "{\"timeDiffGroup\":1800,\"resizeWidth\":1000,\"resizeHeight\":1000}";

		mvcResult = this.mockMvcPerformGetAuthorizationDefaultUserInternal(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);	
	}
	
	@Test
	void testGetResetToDefaultNoDefaultParametersNotFound404() throws Exception {
		String requestUrl = "/parameters-reset-to-default";
		int expectedStatus = 404;
		String expectedJson = "";
		
		parametersDefaultRepository.deleteAll();
		
		ResultActions mvcResult = this.mockMvcPerformGetAuthorizationDefaultUserInternal(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}	
	
}