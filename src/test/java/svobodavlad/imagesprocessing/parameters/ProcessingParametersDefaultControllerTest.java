package svobodavlad.imagesprocessing.parameters;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;

import svobodavlad.imagesprocessing.jpaentities.ProcessingParametersDefault;
import svobodavlad.imagesprocessing.testutil.UnitTestTemplateMockMvc;

@WebMvcTest(ProcessingParametersDefaultController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ProcessingParametersDefaultControllerTest extends UnitTestTemplateMockMvc {
	
	private static final String ADMIN_PARAMETERS_DEFAULT_URL = "/admin/parameters-default";
	
	private static final int TIME_DIFF_GROUP = 1800;
	private static final int RESIZE_HEIGHT = 1000;
	private static final int RESIZE_WIDTH = 1000;
	
	private static final int TIME_DIFF_GROUP_UPDATED = 3600;
	
	private static final int HTTP_OK = 200;
	private static final int HTTP_NOT_FOUND = 404;
	
	@MockBean
	private ProcessingParametersDefaultRepository parametersRepository;
	
    @Autowired
    private JacksonTester<ProcessingParametersDefaultTemplate> jacksonTester;	
	
	@Test
	void getProcessingParametersDefaultTemplateOk200() throws Exception {
		var parametersList = new ArrayList<ProcessingParametersDefault>();
		var parameters = new ProcessingParametersDefault().setTimeDiffGroup(TIME_DIFF_GROUP).setResizeHeight(RESIZE_HEIGHT).setResizeWidth(RESIZE_WIDTH);
		parameters.setId(1);
		parametersList.add(parameters);
		this.given(parametersRepository.findAll()).willReturn(parametersList);
		var expectedJson = jacksonTester.write(parameters.toProcessingParametersDefaultTemplate()).getJson();
		
		var mvcResult = this.mockMvcPerformGetNoAuthorization(ADMIN_PARAMETERS_DEFAULT_URL);
		this.mockMvcExpectStatusAndContent(mvcResult, HTTP_OK, expectedJson);
	}
	
	@Test
	void getProcessingParametersDefaultTemplateNotFound404() throws Exception {
		this.given(parametersRepository.findAll()).willReturn(new ArrayList<ProcessingParametersDefault>());
		
		var mvcResult = this.mockMvcPerformGetNoAuthorization(ADMIN_PARAMETERS_DEFAULT_URL);
		this.mockMvcExpectStatusAndContent(mvcResult, HTTP_NOT_FOUND, "");
	}	
	
	@Test
	void updateProcessingParametersDefaultTemplateOk200() throws Exception {
		var parameters = new ProcessingParametersDefault().setTimeDiffGroup(TIME_DIFF_GROUP_UPDATED).setResizeHeight(RESIZE_HEIGHT).setResizeWidth(RESIZE_WIDTH);
		parameters.setId(1);
		this.given(parametersRepository.findAll()).willReturn(new ArrayList<ProcessingParametersDefault>(List.of(parameters)));
		this.given(parametersRepository.save(parameters)).willReturn(parameters);
		var requestJson = jacksonTester.write(parameters.toProcessingParametersDefaultTemplate()).getJson();
		var expectedJson = requestJson;
		
		var mvcResult = this.mockMvcPerformPutNoAuthorization(ADMIN_PARAMETERS_DEFAULT_URL, requestJson);
		this.mockMvcExpectStatusAndContent(mvcResult, HTTP_OK, expectedJson);
	}	
	
	@Test
	void updateProcessingParametersDefaultTemplateNotFound404() throws Exception {
		this.given(parametersRepository.findAll()).willReturn(new ArrayList<ProcessingParametersDefault>());
		var template = new ProcessingParametersDefaultTemplate().setTimeDiffGroup(TIME_DIFF_GROUP_UPDATED).setResizeHeight(RESIZE_HEIGHT).setResizeWidth(RESIZE_WIDTH);
		var requestJson = jacksonTester.write(template).getJson();
		
		var mvcResult = this.mockMvcPerformPutNoAuthorization(ADMIN_PARAMETERS_DEFAULT_URL, requestJson);
		this.mockMvcExpectStatusAndContent(mvcResult, HTTP_NOT_FOUND, "");
	}
	
}