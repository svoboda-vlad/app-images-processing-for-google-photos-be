package svobodavlad.imagesprocessing.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import svobodavlad.imagesprocessing.parameters.ProcessingParametersDefault;
import svobodavlad.imagesprocessing.parameters.ProcessingParametersDefaultRepository;
import svobodavlad.imagesprocessing.parameters.ProcessingParametersUser;
import svobodavlad.imagesprocessing.parameters.ProcessingParametersUserRepository;
import svobodavlad.imagesprocessing.security.User;
import svobodavlad.imagesprocessing.testutil.SecurityTestUtil;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
//@WithMockUser - not needed
public class ProcessingParametersUserControllerIntegTest {

	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private SecurityTestUtil securityTestUtil;
	
	@Autowired
	private ProcessingParametersUserRepository parametersRepository;
	
	@Autowired
	private ProcessingParametersDefaultRepository parametersDefaultRepository;	
		
	@BeforeEach
	void initData() {
		User defaultUser = securityTestUtil.saveDefaultUser();
		parametersRepository.save(new ProcessingParametersUser(0L, 3600, 1000, 1000, defaultUser));
		parametersDefaultRepository.save(new ProcessingParametersDefault(0L, 1800, 1000, 1000));
	}

	@Test
	void testGetProcessingParametersUserTemplateOk200() throws Exception {		
		String requestUrl = "/parameters";
		int expectedStatus = 200;
		String expectedJson = "{\"timeDiffGroup\":3600,\"resizeWidth\":1000,\"resizeHeight\":1000}";

		this.mvc.perform(get(requestUrl).header("Authorization", SecurityTestUtil.createBearerTokenDefaultUser())
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().is(expectedStatus))
				.andExpect(content().json(expectedJson));		
	}
	
	@Test
	void testGetProcessingParametersUserTemplateNotFound404() throws Exception {
		String requestUrl = "/parameters";
		int expectedStatus = 404;
		String expectedJson = "";
		
		parametersRepository.deleteAll();

		this.mvc.perform(get(requestUrl).header("Authorization", SecurityTestUtil.createBearerTokenDefaultUser())
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));		
	}	

	@Test
	void testUpdateProcessingParametersUserTemplateOk200() throws Exception {
		String requestUrl = "/parameters";
		String requestJson = "{\"timeDiffGroup\":7200,\"resizeWidth\":1000,\"resizeHeight\":1000}";
		int expectedStatus = 200;
		String expectedJson = "{\"timeDiffGroup\":7200,\"resizeWidth\":1000,\"resizeHeight\":1000}";

		this.mvc.perform(put(requestUrl).content(requestJson).header("Authorization", SecurityTestUtil.createBearerTokenDefaultUser())
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(expectedStatus))
				.andExpect(content().json(expectedJson));
	}
	
	@Test
	void testUpdateProcessingParametersUserTemplateNotFound404() throws Exception {
		String requestUrl = "/parameters";
		String requestJson = "{\"timeDiffGroup\":3600,\"resizeWidth\":1000,\"resizeHeight\":1000}";
		int expectedStatus = 404;
		String expectedJson = "";
		
		parametersRepository.deleteAll();

		this.mvc.perform(put(requestUrl).content(requestJson).header("Authorization", SecurityTestUtil.createBearerTokenDefaultUser())
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
	}
	
	@Test
	void testGetResetToDefaultOk200() throws Exception {
		String requestUrl = "/parameters-reset-to-default";
		int expectedStatus = 200;
		String expectedJson = "";
				
		this.mvc.perform(get(requestUrl).header("Authorization", SecurityTestUtil.createBearerTokenDefaultUser())
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
		
		requestUrl = "/parameters";
		expectedStatus = 200;
		expectedJson = "{\"timeDiffGroup\":1800,\"resizeWidth\":1000,\"resizeHeight\":1000}";

		this.mvc.perform(get(requestUrl).header("Authorization", SecurityTestUtil.createBearerTokenDefaultUser())
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().is(expectedStatus))
				.andExpect(content().json(expectedJson));
	}
		
	@Test
	void testGetResetToDefaultNoUserParametersOk200() throws Exception {
		String requestUrl = "/parameters-reset-to-default";
		int expectedStatus = 200;
		String expectedJson = "";
		
		parametersRepository.deleteAll();
				
		this.mvc.perform(get(requestUrl).header("Authorization", SecurityTestUtil.createBearerTokenDefaultUser())
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
		
		requestUrl = "/parameters";
		expectedStatus = 200;
		expectedJson = "{\"timeDiffGroup\":1800,\"resizeWidth\":1000,\"resizeHeight\":1000}";

		this.mvc.perform(get(requestUrl).header("Authorization", SecurityTestUtil.createBearerTokenDefaultUser())
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().is(expectedStatus))
				.andExpect(content().json(expectedJson));		
	}
	
	@Test
	void testGetResetToDefaultNoDefaultParametersNotFound404() throws Exception {
		String requestUrl = "/parameters-reset-to-default";
		int expectedStatus = 404;
		String expectedJson = "";
		
		parametersDefaultRepository.deleteAll();
		
		this.mvc.perform(get(requestUrl).header("Authorization", SecurityTestUtil.createBearerTokenDefaultUser())
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
	}	
	
}