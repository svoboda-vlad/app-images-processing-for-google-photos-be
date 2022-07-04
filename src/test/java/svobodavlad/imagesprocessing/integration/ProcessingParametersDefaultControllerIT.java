package svobodavlad.imagesprocessing.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;

import svobodavlad.imagesprocessing.jpaentities.ProcessingParametersDefault;
import svobodavlad.imagesprocessing.parameters.ProcessingParametersDefaultRepository;
import svobodavlad.imagesprocessing.parameters.ProcessingParametersDefaultTemplate;
import svobodavlad.imagesprocessing.testutil.IntegTestTemplate;

public class ProcessingParametersDefaultControllerIT extends IntegTestTemplate {
	
	private static final int TIME_DIFF_GROUP = 1800;
	private static final int RESIZE_HEIGHT = 1000;
	private static final int RESIZE_WIDTH = 1000;
	
	private static final int TIME_DIFF_GROUP_UPDATED = 3600;	
	
	private static final String ADMIN_PARAMETERS_DEFAULT_URL = "/admin/parameters-default";
	
	private static final int HTTP_OK = 200;
	private static final int HTTP_NOT_FOUND = 404;	
		
	@Autowired
	private ProcessingParametersDefaultRepository parametersRepository;
	
    @Autowired
    private JacksonTester<ProcessingParametersDefaultTemplate> jacksonTester;	
	
	@BeforeEach
	void initData() {
		if (parametersRepository.findAll().size() == 0) {
			parametersRepository.save(new ProcessingParametersDefault().setTimeDiffGroup(TIME_DIFF_GROUP).setResizeHeight(RESIZE_HEIGHT).setResizeWidth(RESIZE_WIDTH));			
		}
	}

	@Test
	void getProcessingParametersDefaultTemplateOk200() throws Exception {		
		var template = new ProcessingParametersDefaultTemplate().setTimeDiffGroup(TIME_DIFF_GROUP).setResizeHeight(RESIZE_HEIGHT).setResizeWidth(RESIZE_WIDTH);
		var expectedJson = jacksonTester.write(template).getJson();
		
		var mvcResult = mockMvcPerformGetAuthorizationAdminUser(ADMIN_PARAMETERS_DEFAULT_URL);
		mockMvcExpectStatusAndContent(mvcResult, HTTP_OK, expectedJson);
	}
	
	@Test
	void getProcessingParametersDefaultTemplateNotFound404() throws Exception {
		parametersRepository.deleteAll();		

		var mvcResult = mockMvcPerformGetAuthorizationAdminUser(ADMIN_PARAMETERS_DEFAULT_URL);
		mockMvcExpectStatusAndContent(mvcResult, HTTP_NOT_FOUND, "");		
	}	

	@Test
	void updateProcessingParametersDefaultTemplateOk200() throws Exception {
		var template = new ProcessingParametersDefaultTemplate().setTimeDiffGroup(TIME_DIFF_GROUP_UPDATED).setResizeHeight(RESIZE_HEIGHT).setResizeWidth(RESIZE_WIDTH);
		var requestJson = jacksonTester.write(template).getJson();
		var expectedJson = requestJson;

		var mvcResult = mockMvcPerformPutAuthorizationAdminUser(ADMIN_PARAMETERS_DEFAULT_URL, requestJson);
		mockMvcExpectStatusAndContent(mvcResult, HTTP_OK, expectedJson);
	}
	
	@Test
	void updateProcessingParametersDefaultTemplateNotFound404() throws Exception {
		var template = new ProcessingParametersDefaultTemplate().setTimeDiffGroup(TIME_DIFF_GROUP_UPDATED).setResizeHeight(RESIZE_HEIGHT).setResizeWidth(RESIZE_WIDTH);
		var requestJson = jacksonTester.write(template).getJson();
		parametersRepository.deleteAll();

		var mvcResult = mockMvcPerformPutAuthorizationAdminUser(ADMIN_PARAMETERS_DEFAULT_URL, requestJson);
		mockMvcExpectStatusAndContent(mvcResult, HTTP_NOT_FOUND, "");
	}	
}