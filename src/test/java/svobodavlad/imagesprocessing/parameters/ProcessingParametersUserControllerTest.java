package svobodavlad.imagesprocessing.parameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;

import svobodavlad.imagesprocessing.jpaentities.ProcessingParametersDefault;
import svobodavlad.imagesprocessing.jpaentities.ProcessingParametersUser;
import svobodavlad.imagesprocessing.jpaentities.User;
import svobodavlad.imagesprocessing.testutil.UnitTestTemplateMockMvc;

@WebMvcTest(ProcessingParametersUserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ProcessingParametersUserControllerTest extends UnitTestTemplateMockMvc {
	
	private static final String MOCKED_USER_NAME = "user";
	
	private static final int TIME_DIFF_GROUP = 1800;
	private static final int RESIZE_HEIGHT = 1000;
	private static final int RESIZE_WIDTH = 1000;
	
	private static final int TIME_DIFF_GROUP_UPDATED = 3600;
	
	private static final String PARAMETERS_URL = "/parameters";
	private static final String PARAMETERS_RESET_TO_DEFAULT_URL = "/parameters-reset-to-default";
	
	private static final int HTTP_OK = 200;
	private static final int HTTP_NO_CONTENT = 204;
	private static final int HTTP_NOT_FOUND = 404;	
	
	@MockBean
	private ProcessingParametersDefaultRepository parametersDefaultRepository;
	
	@MockBean
	private ProcessingParametersUserService parametersService;
	
    @Autowired
    private JacksonTester<ProcessingParametersUserTemplate> jacksonTester;		

	@Test
	void getProcessingParametersUserTemplateOk200() throws Exception {
		var mockedUser = new User().setUsername(MOCKED_USER_NAME).setGivenName(MOCKED_USER_NAME).setFamilyName(MOCKED_USER_NAME);
		var parameters = new ProcessingParametersUser().setTimeDiffGroup(TIME_DIFF_GROUP).setResizeHeight(RESIZE_HEIGHT).setResizeWidth(RESIZE_WIDTH)
				.setUser(mockedUser);
		this.given(parametersService.getForCurrentUser()).willReturn(Optional.of(parameters));
		var expectedJson = jacksonTester.write(parameters.toProcessingParametersUserTemplate()).getJson();
		
		var mvcResult = this.mockMvcPerformGetNoAuthorization(PARAMETERS_URL);
		this.mockMvcExpectStatusAndContent(mvcResult, HTTP_OK, expectedJson);		
	}
	
	@Test
	void getProcessingParametersUserTemplateNoParametersNotFound404() throws Exception {
		this.given(parametersService.getForCurrentUser()).willReturn(Optional.empty());
		
		var mvcResult = this.mockMvcPerformGetNoAuthorization(PARAMETERS_URL);
		this.mockMvcExpectStatusAndContent(mvcResult, HTTP_NOT_FOUND, "");
	}

	@Test
	void updateProcessingParametersUserTemplateOk200() throws Exception {
		var mockedUser = new User().setUsername(MOCKED_USER_NAME).setGivenName(MOCKED_USER_NAME).setFamilyName(MOCKED_USER_NAME);
		var parameters = new ProcessingParametersUser().setTimeDiffGroup(TIME_DIFF_GROUP).setResizeHeight(RESIZE_HEIGHT).setResizeWidth(RESIZE_WIDTH).setUser(mockedUser);
		var template = parameters.toProcessingParametersUserTemplate();
		this.given(parametersService.updateForCurrentUser(template)).willReturn(Optional.of(parameters));
		var requestJson = jacksonTester.write(template).getJson();
		var expectedJson = requestJson;
		
		var mvcResult = this.mockMvcPerformPutNoAuthorization(PARAMETERS_URL, requestJson);
		this.mockMvcExpectStatusAndContent(mvcResult, HTTP_OK, expectedJson);
	}
	
	
	@Test
	void updateProcessingParametersDefaultNotFound404() throws Exception {
		var parametersTemplate = new ProcessingParametersUserTemplate().setTimeDiffGroup(TIME_DIFF_GROUP).setResizeHeight(RESIZE_HEIGHT).setResizeWidth(RESIZE_WIDTH);
		this.given(parametersService.updateForCurrentUser(parametersTemplate)).willReturn(Optional.empty());
		var requestJson = jacksonTester.write(parametersTemplate).getJson();
		
		var mvcResult = this.mockMvcPerformPutNoAuthorization(PARAMETERS_URL, requestJson);
		this.mockMvcExpectStatusAndContent(mvcResult, HTTP_NOT_FOUND, "");
	}
	
	@Test
	void getResetToDefaultOk204() throws Exception {
		var parameters = new ProcessingParametersDefault().setTimeDiffGroup(TIME_DIFF_GROUP_UPDATED).setResizeHeight(RESIZE_HEIGHT).setResizeWidth(RESIZE_WIDTH);
		this.given(parametersDefaultRepository.findAll()).willReturn(new ArrayList<ProcessingParametersDefault>(List.of(parameters)));
		
		var mvcResult = this.mockMvcPerformGetNoAuthorization(PARAMETERS_RESET_TO_DEFAULT_URL);
		this.mockMvcExpectStatusAndContent(mvcResult, HTTP_NO_CONTENT, "");
		this.verify(this.parametersService, this.times(1)).resetToDefault();
	}
	
	@Test
	void getResetToDefaultNoDefaultParametersNotFound404() throws Exception {
		this.given(parametersDefaultRepository.findAll()).willReturn(new ArrayList<ProcessingParametersDefault>());
		
		var mvcResult = this.mockMvcPerformGetNoAuthorization(PARAMETERS_RESET_TO_DEFAULT_URL);
		this.mockMvcExpectStatusAndContent(mvcResult, HTTP_NOT_FOUND, "");
		this.verify(this.parametersService, never()).resetToDefault();		
	}	
	
}