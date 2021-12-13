package svobodavlad.imagesprocessing.parameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;

import svobodavlad.imagesprocessing.jpaentities.ProcessingParametersDefault;
import svobodavlad.imagesprocessing.jpaentities.ProcessingParametersUser;
import svobodavlad.imagesprocessing.jpaentities.User;
import svobodavlad.imagesprocessing.testutil.SecurityMockUtil;
import svobodavlad.imagesprocessing.testutil.UnitTestTemplate;

@WithMockUser(username = SecurityMockUtil.DEFAULT_USERNAME)
public class ProcessingParametersUserControllerTest extends UnitTestTemplate {
	
	@MockBean
	private ProcessingParametersDefaultRepository parametersDefaultRepository;
	
	@MockBean
	private ProcessingParametersUserService parametersService;	
	
	private User mockedUser;

	@BeforeEach
	private void initData() {
		mockedUser = SecurityMockUtil.getMockedDefaultUserInternal();
	}

	@Test
	void testGetProcessingParametersUserTemplateOk200() throws Exception {
		String requestUrl = "/parameters";
		int expectedStatus = 200;
		String expectedJson = "{\"timeDiffGroup\":1800,\"resizeWidth\":1000,\"resizeHeight\":1000}";
		
		ProcessingParametersUser parameters = new ProcessingParametersUser(1800, 1000, 1000, mockedUser);
		parameters.setId(1);
		
		this.given(parametersService.getForCurrentUser()).willReturn(Optional.of(parameters));
		
		ResultActions mvcResult = this.mockMvcPerformGetNoAuthorization(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);		
	}
	
	@Test
	void testGetProcessingParametersUserTemplateNoParametersNotFound404() throws Exception {
		String requestUrl = "/parameters";
		int expectedStatus = 404;
		String expectedJson = "";
		
		this.given(parametersService.getForCurrentUser()).willReturn(Optional.empty());
		
		ResultActions mvcResult = this.mockMvcPerformGetNoAuthorization(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}

	@Test
	void testUpdateProcessingParametersUserTemplateOk200() throws Exception {
		String requestUrl = "/parameters";
		String requestJson = "{\"timeDiffGroup\":3600,\"resizeWidth\":1000,\"resizeHeight\":1000}";
		int expectedStatus = 200;
		String expectedJson = "{\"timeDiffGroup\":3600,\"resizeWidth\":1000,\"resizeHeight\":1000}";
		
		ProcessingParametersUser parameters = new ProcessingParametersUser(3600, 1000, 1000, mockedUser);
		parameters.setId(1);

		ProcessingParametersUserTemplate template = parameters.toProcessingParametersUserTemplate();
		
		this.given(parametersService.updateForCurrentUser(template)).willReturn(parameters);
		
		ResultActions mvcResult = this.mockMvcPerformPutNoAuthorization(requestUrl, requestJson);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}
	
	
	@Test
	void testUpdateProcessingParametersDefaultNotFound404() throws Exception {
		String requestUrl = "/parameters";
		String requestJson = "{\"timeDiffGroup\":3600,\"resizeWidth\":1000,\"resizeHeight\":1000}";
		int expectedStatus = 404;
		String expectedJson = "";
		
		ProcessingParametersUser parameters = new ProcessingParametersUser(3600, 1000, 1000, mockedUser);
		parameters.setId(1);
		
		ProcessingParametersUserTemplate template = parameters.toProcessingParametersUserTemplate();
		
		this.given(parametersService.updateForCurrentUser(template)).willReturn(null);
				
		ResultActions mvcResult = this.mockMvcPerformPutNoAuthorization(requestUrl, requestJson);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}
	
	@Test
	void testGetResetToDefaultOk200() throws Exception {
		String requestUrl = "/parameters-reset-to-default";
		int expectedStatus = 200;
		String expectedJson = "";
		
		ProcessingParametersDefault parameters = new ProcessingParametersDefault(3600, 1000, 1000);
		this.given(parametersDefaultRepository.findAll()).willReturn(new ArrayList<ProcessingParametersDefault>(List.of(parameters)));
		
		ResultActions mvcResult = this.mockMvcPerformGetNoAuthorization(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
		
		this.verify(this.parametersService, this.times(1)).resetToDefault();
	}
	
	@Test
	void testGetResetToDefaultNoDefaultParametersNotFound404() throws Exception {
		String requestUrl = "/parameters-reset-to-default";
		int expectedStatus = 404;
		String expectedJson = "";
		
		this.given(parametersDefaultRepository.findAll()).willReturn(new ArrayList<ProcessingParametersDefault>());
				
		ResultActions mvcResult = this.mockMvcPerformGetNoAuthorization(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
		
		this.verify(this.parametersService, never()).resetToDefault();		
	}	
	
}