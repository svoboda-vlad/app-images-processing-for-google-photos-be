package svobodavlad.imagesprocessing.parameters;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

import svobodavlad.imagesprocessing.jpaentities.ProcessingParametersDefault;
import svobodavlad.imagesprocessing.testutil.UnitTestTemplate;

public class ProcessingParametersDefaultControllerTest extends UnitTestTemplate {
	
	@MockBean
	private ProcessingParametersDefaultRepository parametersRepository;

	@Test
	void getProcessingParametersDefaultTemplateOk200() throws Exception {
		String requestUrl = "/admin/parameters-default";
		int expectedStatus = 200;
		String expectedJson = "{\"timeDiffGroup\":1800,\"resizeWidth\":1000,\"resizeHeight\":1000}";
		
		List<ProcessingParametersDefault> parametersList = new ArrayList<ProcessingParametersDefault>();
		ProcessingParametersDefault parameters = new ProcessingParametersDefault(1800, 1000, 1000);
		parameters.setId(1);
		parametersList.add(parameters);
		
		this.given(parametersRepository.findAll()).willReturn(parametersList);
		
		ResultActions mvcResult = this.mockMvcPerformGetNoAuthorization(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}
	
	@Test
	void getProcessingParametersDefaultTemplateNotFound404() throws Exception {
		String requestUrl = "/admin/parameters-default";
		int expectedStatus = 404;
		String expectedJson = "";
		
		this.given(parametersRepository.findAll()).willReturn(new ArrayList<ProcessingParametersDefault>());
		
		ResultActions mvcResult = this.mockMvcPerformGetNoAuthorization(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}	
	
	@Test
	void updateProcessingParametersDefaultTemplateOk200() throws Exception {
		String requestUrl = "/admin/parameters-default";
		String requestJson = "{\"timeDiffGroup\":3600,\"resizeWidth\":1000,\"resizeHeight\":1000}";
		int expectedStatus = 200;
		String expectedJson = "{\"timeDiffGroup\":3600,\"resizeWidth\":1000,\"resizeHeight\":1000}";
		
		ProcessingParametersDefault parameters = new ProcessingParametersDefault(3600, 1000, 1000);
		parameters.setId(1);
		this.given(parametersRepository.findAll()).willReturn(new ArrayList<ProcessingParametersDefault>(List.of(parameters)));
		this.given(parametersRepository.save(parameters)).willReturn(parameters);
		
		ResultActions mvcResult = this.mockMvcPerformPutNoAuthorization(requestUrl, requestJson);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}	
	
	@Test
	void updateProcessingParametersDefaultTemplateNotFound404() throws Exception {
		String requestUrl = "/admin/parameters-default";
		String requestJson = "{\"timeDiffGroup\":3600,\"resizeWidth\":1000,\"resizeHeight\":1000}";
		int expectedStatus = 404;
		String expectedJson = "";
		
		this.given(parametersRepository.findAll()).willReturn(new ArrayList<ProcessingParametersDefault>());
		
		ResultActions mvcResult = this.mockMvcPerformPutNoAuthorization(requestUrl, requestJson);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}
	
}