package svobodavlad.imagesprocessing.parameters;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import svobodavlad.imagesprocessing.security.User;
import svobodavlad.imagesprocessing.security.UserRepository;
import svobodavlad.imagesprocessing.testutil.SecurityMockUtil;
import svobodavlad.imagesprocessing.testutil.SecurityTestUtil;

@SpringBootTest
@AutoConfigureMockMvc
//@WithMockUser - not needed
public class ProcessingParametersUserControllerTest {

	@Autowired
	private MockMvc mvc;
	
	@MockBean
	private ProcessingParametersUserRepository parametersRepository;
	
	@MockBean
	private UserDetailsService userDetailsService;

	@MockBean
	private UserRepository userRepository;
	
	@MockBean
	private ProcessingParametersDefaultRepository parametersDefaultRepository;	
	
	private User mockedUser;

	@BeforeEach
	private void initData() {
		mockedUser = SecurityMockUtil.getMockedDefaultUser();
		given(userDetailsService.loadUserByUsername(mockedUser.getUsername())).willReturn(mockedUser);
	}

	@Test
	void testGetProcessingParametersUserTemplateOk200() throws Exception {
		String requestUrl = "/parameters";
		int expectedStatus = 200;
		String expectedJson = "{\"timeDiffGroup\":1800,\"resizeWidth\":1000,\"resizeHeight\":1000}";
		
		ProcessingParametersUser parameters = new ProcessingParametersUser(0L, 1800, 1000, 1000, mockedUser);
		parameters.setId(1);
		
		given(userRepository.findByUsername(mockedUser.getUsername())).willReturn(Optional.of(mockedUser));
		given(parametersRepository.findByUser(mockedUser)).willReturn(Optional.of(parameters));

		this.mvc.perform(get(requestUrl).header("Authorization", SecurityTestUtil.createBearerTokenDefaultUser())
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().is(expectedStatus))
				.andExpect(content().json(expectedJson));		
	}
	
	@Test
	void testGetProcessingParametersUserTemplateNoParametersNotFound404() throws Exception {
		String requestUrl = "/parameters";
		int expectedStatus = 404;
		String expectedJson = "";
		
		given(userRepository.findByUsername(mockedUser.getUsername())).willReturn(Optional.of(mockedUser));
		given(parametersRepository.findByUser(mockedUser)).willReturn(Optional.empty());

		this.mvc.perform(get(requestUrl).header("Authorization", SecurityTestUtil.createBearerTokenDefaultUser())
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
	}

	@Test
	void testUpdateProcessingParametersUserTemplateOk200() throws Exception {
		String requestUrl = "/parameters";
		String requestJson = "{\"timeDiffGroup\":3600,\"resizeWidth\":1000,\"resizeHeight\":1000}";
		int expectedStatus = 200;
		String expectedJson = "{\"timeDiffGroup\":3600,\"resizeWidth\":1000,\"resizeHeight\":1000}";
		
		ProcessingParametersUser parameters = new ProcessingParametersUser(0L, 3600, 1000, 1000, mockedUser);
		parameters.setId(1);
		
		given(userRepository.findByUsername(mockedUser.getUsername())).willReturn(Optional.of(mockedUser));
		given(parametersRepository.findByUser(mockedUser)).willReturn(Optional.of(parameters));
		given(parametersRepository.save(parameters)).willReturn(parameters);
		
		this.mvc.perform(put(requestUrl).content(requestJson).header("Authorization", SecurityTestUtil.createBearerTokenDefaultUser())
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(expectedStatus))
				.andExpect(content().json(expectedJson));
	}	
	
	
	@Test
	void testUpdateProcessingParametersDefaultNotFound404() throws Exception {
		String requestUrl = "/parameters";
		String requestJson = "{\"timeDiffGroup\":3600,\"resizeWidth\":1000,\"resizeHeight\":1000}";
		int expectedStatus = 404;
		String expectedJson = "";
		
		ProcessingParametersUser parameters = new ProcessingParametersUser(0L, 3600, 1000, 1000, mockedUser);
		parameters.setId(1);
		
		given(userRepository.findByUsername(mockedUser.getUsername())).willReturn(Optional.of(mockedUser));
		given(parametersRepository.findByUser(mockedUser)).willReturn(Optional.empty());
		
		this.mvc.perform(put(requestUrl).content(requestJson).header("Authorization", SecurityTestUtil.createBearerTokenDefaultUser())
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
	}
	
	@Test
	void testGetRresetToDefaultOk200() throws Exception {
		String requestUrl = "/parameters-reset-to-default";
		int expectedStatus = 200;
		String expectedJson = "";
		
		ProcessingParametersUser parameters = new ProcessingParametersUser(0L, 3600, 1000, 1000, mockedUser);
		ProcessingParametersDefault parametersDefault = new ProcessingParametersDefault(0L, 1800, 1000, 1000);
		
		given(userRepository.findByUsername(mockedUser.getUsername())).willReturn(Optional.of(mockedUser));
		given(parametersRepository.findByUser(mockedUser)).willReturn(Optional.of(parameters));
		given(parametersDefaultRepository.findAll()).willReturn(new ArrayList<ProcessingParametersDefault>(List.of(parametersDefault)));
		ProcessingParametersUser parametersReset = parameters.resetToDefault(parametersDefault);
		given(parametersRepository.save(parametersReset)).willReturn(parametersReset);
		
		this.mvc.perform(get(requestUrl).header("Authorization", SecurityTestUtil.createBearerTokenDefaultUser())
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
	}
	
	
	@Test
	void testGetRresetToDefaultNoUserParametersNotFound404() throws Exception {
		String requestUrl = "/parameters-reset-to-default";
		int expectedStatus = 404;
		String expectedJson = "";
				
		given(userRepository.findByUsername(mockedUser.getUsername())).willReturn(Optional.of(mockedUser));
		given(parametersRepository.findByUser(mockedUser)).willReturn(Optional.empty());
		
		this.mvc.perform(get(requestUrl).header("Authorization", SecurityTestUtil.createBearerTokenDefaultUser())
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
	}
	
	
	@Test
	void testGetRresetToDefaultNoDefaultParametersNotFound404() throws Exception {
		String requestUrl = "/parameters-reset-to-default";
		int expectedStatus = 404;
		String expectedJson = "";
		
		ProcessingParametersUser parameters = new ProcessingParametersUser(0L, 3600, 1000, 1000, mockedUser);
		
		given(userRepository.findByUsername(mockedUser.getUsername())).willReturn(Optional.of(mockedUser));
		given(parametersRepository.findByUser(mockedUser)).willReturn(Optional.of(parameters));
		given(parametersDefaultRepository.findAll()).willReturn(new ArrayList<ProcessingParametersDefault>());
		
		this.mvc.perform(get(requestUrl).header("Authorization", SecurityTestUtil.createBearerTokenDefaultUser())
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
	}	
	
}