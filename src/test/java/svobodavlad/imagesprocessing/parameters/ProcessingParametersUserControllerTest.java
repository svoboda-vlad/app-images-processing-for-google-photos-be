package svobodavlad.imagesprocessing.parameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.ResultActions;

import svobodavlad.imagesprocessing.security.User;
import svobodavlad.imagesprocessing.security.UserRepository;
import svobodavlad.imagesprocessing.testutil.SecurityMockUtil;
import svobodavlad.imagesprocessing.testutil.UnitTestTemplate;

public class ProcessingParametersUserControllerTest extends UnitTestTemplate {
	
	@MockBean
	private ProcessingParametersUserRepository parametersRepository;
	
	@MockBean
	private ProcessingParametersDefaultRepository parametersDefaultRepository;	
	
	@MockBean
	private UserDetailsService userDetailsService;

	@MockBean
	private UserRepository userRepository;
	
	@MockBean
	private ProcessingParametersUserService parametersService;	
	
	private User mockedUser;

	@BeforeEach
	private void initData() {
		mockedUser = SecurityMockUtil.getMockedDefaultUserInternal();
		this.given(userDetailsService.loadUserByUsername(mockedUser.getUsername())).willReturn(mockedUser);
	}

	@Test
	void testGetProcessingParametersUserTemplateOk200() throws Exception {
		String requestUrl = "/parameters";
		int expectedStatus = 200;
		String expectedJson = "{\"timeDiffGroup\":1800,\"resizeWidth\":1000,\"resizeHeight\":1000}";
		
		ProcessingParametersUser parameters = new ProcessingParametersUser(1800, 1000, 1000);
		parameters.setUser(mockedUser);
		parameters.setId(1);
		
		this.given(userRepository.findByUsername(mockedUser.getUsername())).willReturn(Optional.of(mockedUser));
		this.given(parametersRepository.findByUser(mockedUser)).willReturn(Optional.of(parameters));
		
		ResultActions mvcResult = this.mockMvcPerformGetAuthorizationDefaultUser(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);		
	}
	
	@Test
	void testGetProcessingParametersUserTemplateNoParametersNotFound404() throws Exception {
		String requestUrl = "/parameters";
		int expectedStatus = 404;
		String expectedJson = "";
		
		this.given(userRepository.findByUsername(mockedUser.getUsername())).willReturn(Optional.of(mockedUser));
		this.given(parametersRepository.findByUser(mockedUser)).willReturn(Optional.empty());
		
		ResultActions mvcResult = this.mockMvcPerformGetAuthorizationDefaultUser(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}

	@Test
	void testUpdateProcessingParametersUserTemplateOk200() throws Exception {
		String requestUrl = "/parameters";
		String requestJson = "{\"timeDiffGroup\":3600,\"resizeWidth\":1000,\"resizeHeight\":1000}";
		int expectedStatus = 200;
		String expectedJson = "{\"timeDiffGroup\":3600,\"resizeWidth\":1000,\"resizeHeight\":1000}";
		
		ProcessingParametersUser parameters = new ProcessingParametersUser(3600, 1000, 1000);
		parameters.setUser(mockedUser);
		parameters.setId(1);
		
		this.given(userRepository.findByUsername(mockedUser.getUsername())).willReturn(Optional.of(mockedUser));
		this.given(parametersRepository.findByUser(mockedUser)).willReturn(Optional.of(parameters));
		this.given(parametersRepository.save(parameters)).willReturn(parameters);
		
		ResultActions mvcResult = this.mockMvcPerformPutAuthorizationDefaultUser(requestUrl, requestJson);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}	
	
	
	@Test
	void testUpdateProcessingParametersDefaultNotFound404() throws Exception {
		String requestUrl = "/parameters";
		String requestJson = "{\"timeDiffGroup\":3600,\"resizeWidth\":1000,\"resizeHeight\":1000}";
		int expectedStatus = 404;
		String expectedJson = "";
		
		ProcessingParametersUser parameters = new ProcessingParametersUser(3600, 1000, 1000);
		parameters.setUser(mockedUser);
		parameters.setId(1);
		
		this.given(userRepository.findByUsername(mockedUser.getUsername())).willReturn(Optional.of(mockedUser));
		this.given(parametersRepository.findByUser(mockedUser)).willReturn(Optional.empty());
		
		ResultActions mvcResult = this.mockMvcPerformPutAuthorizationDefaultUser(requestUrl, requestJson);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}
	
	@Test
	void testGetResetToDefaultOk200() throws Exception {
		String requestUrl = "/parameters-reset-to-default";
		int expectedStatus = 200;
		String expectedJson = "";
		
		ProcessingParametersDefault parameters = new ProcessingParametersDefault(3600, 1000, 1000);
		this.given(parametersDefaultRepository.findAll()).willReturn(new ArrayList<ProcessingParametersDefault>(List.of(parameters)));
		
		ResultActions mvcResult = this.mockMvcPerformGetAuthorizationDefaultUser(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
		
		this.verify(this.parametersService, this.times(1)).resetToDefault();
	}
	
	@Test
	void testGetResetToDefaultNoDefaultParametersNotFound404() throws Exception {
		String requestUrl = "/parameters-reset-to-default";
		int expectedStatus = 404;
		String expectedJson = "";
		
		this.given(parametersDefaultRepository.findAll()).willReturn(new ArrayList<ProcessingParametersDefault>());
				
		ResultActions mvcResult = this.mockMvcPerformGetAuthorizationDefaultUser(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
		
		this.verify(this.parametersService, never()).resetToDefault();		
	}	
	
}