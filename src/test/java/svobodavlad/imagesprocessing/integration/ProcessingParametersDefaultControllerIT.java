package svobodavlad.imagesprocessing.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import svobodavlad.imagesprocessing.jpaentities.ProcessingParametersDefault;
import svobodavlad.imagesprocessing.parameters.ProcessingParametersDefaultRepository;
import svobodavlad.imagesprocessing.testutil.IntegTestTemplate;
import svobodavlad.imagesprocessing.testutil.SecurityTestUtil;

public class ProcessingParametersDefaultControllerIT extends IntegTestTemplate {
	
	@Autowired
	private SecurityTestUtil securityTestUtil;
	
	@Autowired
	private ProcessingParametersDefaultRepository parametersRepository;
	
	@BeforeEach
	void initData() {
		securityTestUtil.saveAdminUser();
		parametersRepository.save(new ProcessingParametersDefault(1800, 1000, 1000));
	}

	@Test
	void testGetProcessingParametersDefaultTemplateOk200() throws Exception {		
		String requestUrl = "/admin/parameters-default";
		int expectedStatus = 200;
		String expectedJson = "{\"timeDiffGroup\":1800,\"resizeWidth\":1000,\"resizeHeight\":1000}";
		
		ResultActions mvcResult = this.mockMvcPerformGetAuthorizationAdminUser(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}
	
	@Test
	void testGetProcessingParametersDefaultTemplateNotFound404() throws Exception {
		String requestUrl = "/admin/parameters-default";
		int expectedStatus = 404;
		String expectedJson = "";
		
		parametersRepository.deleteAll();		

		ResultActions mvcResult = this.mockMvcPerformGetAuthorizationAdminUser(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);		
	}	

	@Test
	void testUpdateProcessingParametersDefaultTemplateOk200() throws Exception {
		String requestUrl = "/admin/parameters-default";
		String requestJson = "{\"timeDiffGroup\":3600,\"resizeWidth\":1000,\"resizeHeight\":1000}";
		int expectedStatus = 200;
		String expectedJson = "{\"timeDiffGroup\":3600,\"resizeWidth\":1000,\"resizeHeight\":1000}";

		ResultActions mvcResult = this.mockMvcPerformPutAuthorizationAdminUser(requestUrl, requestJson);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}
	
	@Test
	void testUpdateProcessingParametersDefaultTemplateNotFound404() throws Exception {
		String requestUrl = "/admin/parameters-default";
		String requestJson = "{\"timeDiffGroup\":3600,\"resizeWidth\":1000,\"resizeHeight\":1000}";
		int expectedStatus = 404;
		String expectedJson = "";
		
		parametersRepository.deleteAll();

		ResultActions mvcResult = this.mockMvcPerformPutAuthorizationAdminUser(requestUrl, requestJson);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}	
}