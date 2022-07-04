package svobodavlad.imagesprocessing.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;

import svobodavlad.imagesprocessing.parameters.ProcessingParametersDefaultRepository;
import svobodavlad.imagesprocessing.parameters.ProcessingParametersUserRepository;
import svobodavlad.imagesprocessing.parameters.ProcessingParametersUserService;
import svobodavlad.imagesprocessing.parameters.ProcessingParametersUserTemplate;
import svobodavlad.imagesprocessing.testutil.IntegTestTemplate;
import svobodavlad.imagesprocessing.testutil.SecurityTestUtil;

public class ProcessingParametersUserControllerIT extends IntegTestTemplate {
	
	private static final int TIME_DIFF_GROUP = 1800;
	private static final int RESIZE_HEIGHT = 1000;
	private static final int RESIZE_WIDTH = 1000;
	
	private static final int TIME_DIFF_GROUP_UPDATED = 3600;
	
	private static final String PARAMETERS_URL = "/parameters";
	private static final String PARAMETERS_RESET_TO_DEFAULT_URL = "/parameters-reset-to-default";
	
	private static final int HTTP_OK = 200;
	private static final int HTTP_NO_CONTENT = 204;
	private static final int HTTP_NOT_FOUND = 404;	
	
	@Autowired
	private ProcessingParametersUserRepository parametersRepository;
	
	@Autowired
	private ProcessingParametersDefaultRepository parametersDefaultRepository;
	
	@Autowired
	private SecurityTestUtil securityTestUtil;
	
	@Autowired
	private ProcessingParametersUserService parametersService;
	
    @Autowired
    private JacksonTester<ProcessingParametersUserTemplate> jacksonTester;	
	
	@BeforeEach
	void initData() {
		var defaultUser = securityTestUtil.saveDefaultUser();
		parametersService.setInitialParameters(defaultUser.getUsername());
	}
		
	@Test
	void getProcessingParametersUserTemplateOk200() throws Exception {
		var parametersTemplate = new ProcessingParametersUserTemplate().setTimeDiffGroup(TIME_DIFF_GROUP).setResizeHeight(RESIZE_HEIGHT).setResizeWidth(RESIZE_WIDTH);
		var expectedJson = jacksonTester.write(parametersTemplate).getJson();
		
		var mvcResult = mockMvcPerformGetAuthorizationDefaultUser(PARAMETERS_URL);
		mockMvcExpectStatusAndContent(mvcResult, HTTP_OK, expectedJson);
	}
	
	@Test
	void getProcessingParametersUserTemplateNotFound404() throws Exception {
		parametersRepository.deleteAll();

		var mvcResult = mockMvcPerformGetAuthorizationDefaultUser(PARAMETERS_URL);
		mockMvcExpectStatusAndContent(mvcResult, HTTP_NOT_FOUND, "");	
	}	

	@Test
	void updateProcessingParametersUserTemplateOk200() throws Exception {
		var parametersTemplate = new ProcessingParametersUserTemplate().setTimeDiffGroup(TIME_DIFF_GROUP_UPDATED).setResizeHeight(RESIZE_HEIGHT).setResizeWidth(RESIZE_WIDTH);
		var requestJson = jacksonTester.write(parametersTemplate).getJson();
		var expectedJson = requestJson;

		var mvcResult = mockMvcPerformPutAuthorizationDefaultUser(PARAMETERS_URL, requestJson);
		mockMvcExpectStatusAndContent(mvcResult, HTTP_OK, expectedJson);
	}
	
	@Test
	void updateProcessingParametersUserTemplateNotFound404() throws Exception {
		var parametersTemplate = new ProcessingParametersUserTemplate().setTimeDiffGroup(TIME_DIFF_GROUP_UPDATED).setResizeHeight(RESIZE_HEIGHT).setResizeWidth(RESIZE_WIDTH);
		var requestJson = jacksonTester.write(parametersTemplate).getJson();
		parametersRepository.deleteAll();

		var mvcResult = mockMvcPerformPutAuthorizationDefaultUser(PARAMETERS_URL, requestJson);
		mockMvcExpectStatusAndContent(mvcResult, HTTP_NOT_FOUND, "");
	}
	
	@Test
	void getResetToDefaultOk204() throws Exception {
		var mvcResult = mockMvcPerformGetAuthorizationDefaultUser(PARAMETERS_RESET_TO_DEFAULT_URL);
		mockMvcExpectStatusAndContent(mvcResult, HTTP_NO_CONTENT, "");
		
		var parametersTemplate = new ProcessingParametersUserTemplate().setTimeDiffGroup(TIME_DIFF_GROUP).setResizeHeight(RESIZE_HEIGHT).setResizeWidth(RESIZE_WIDTH);
		var expectedJson = jacksonTester.write(parametersTemplate).getJson();
		mvcResult = mockMvcPerformGetAuthorizationDefaultUser(PARAMETERS_URL);
		mockMvcExpectStatusAndContent(mvcResult, HTTP_OK, expectedJson);
	}
		
	@Test
	void getResetToDefaultNoUserParametersOk204() throws Exception {
		parametersRepository.deleteAll();
				
		var mvcResult = mockMvcPerformGetAuthorizationDefaultUser(PARAMETERS_RESET_TO_DEFAULT_URL);
		mockMvcExpectStatusAndContent(mvcResult, HTTP_NO_CONTENT, "");
		
		var parametersTemplate = new ProcessingParametersUserTemplate().setTimeDiffGroup(TIME_DIFF_GROUP).setResizeHeight(RESIZE_HEIGHT).setResizeWidth(RESIZE_WIDTH);
		var expectedJson = jacksonTester.write(parametersTemplate).getJson();
		mvcResult = mockMvcPerformGetAuthorizationDefaultUser(PARAMETERS_URL);
		mockMvcExpectStatusAndContent(mvcResult, HTTP_OK, expectedJson);	
	}
	
	@Test
	void getResetToDefaultNoDefaultParametersNotFound404() throws Exception {
		parametersDefaultRepository.deleteAll();
		
		var mvcResult = mockMvcPerformGetAuthorizationDefaultUser(PARAMETERS_RESET_TO_DEFAULT_URL);
		mockMvcExpectStatusAndContent(mvcResult, HTTP_NOT_FOUND, "");
	}	
	
}