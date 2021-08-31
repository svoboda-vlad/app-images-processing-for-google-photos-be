package svobodavlad.imagesprocessing.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import svobodavlad.imagesprocessing.parametersdefault.ProcessingParametersDefault;
import svobodavlad.imagesprocessing.parametersdefault.ProcessingParametersDefaultRepository;

@SpringBootTest
@AutoConfigureMockMvc
//@Transactional - removed due to false positive tests (error in production: detached entity passed to persist)
//@WithMockUser - not needed
public class ProcessingParametersDefaultControllerIntegTest {

	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private SecurityTestUtil securityTestUtil;
	
	@Autowired
	private ProcessingParametersDefaultRepository parametersRepository;
	
	@BeforeEach
	void initData() {
		securityTestUtil.saveAdminUser();
		parametersRepository.save(new ProcessingParametersDefault(1800, 1000, 1000));
	}

	@AfterEach
	void cleanData() {
		securityTestUtil.deleteAllUsers();
		parametersRepository.deleteAll();		
	}

	@Test
	void testGetProcessingParametersDefaultOk200() throws Exception {		
		String requestUrl = "/admin/parameters-default";
		int expectedStatus = 200;
		String expectedJson = "{\"id\":" + parametersRepository.findAll().get(0).getId() + ",\"timeDiffGroup\":1800,\"resizeWidth\":1000,\"resizeHeight\":1000}";

		this.mvc.perform(get(requestUrl).header("Authorization", SecurityTestUtil.createBearerTokenAdminUser())
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().is(expectedStatus))
				.andExpect(content().json(expectedJson));		
	}
	
	@Test
	void testGetProcessingParametersDefaultNotFound404() throws Exception {
		String requestUrl = "/admin/parameters-default";
		int expectedStatus = 404;
		String expectedJson = "";
		
		parametersRepository.deleteAll();		

		this.mvc.perform(get(requestUrl).header("Authorization", SecurityTestUtil.createBearerTokenAdminUser())
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));		
	}	

	@Test
	void testUpdateProcessingParametersDefaultOk200() throws Exception {
		String requestUrl = "/admin/parameters-default";
		String requestJson = "{\"id\":" + parametersRepository.findAll().get(0).getId() + ",\"timeDiffGroup\":3600,\"resizeWidth\":1000,\"resizeHeight\":1000}";
		int expectedStatus = 200;
		String expectedJson = "{\"id\":" + parametersRepository.findAll().get(0).getId() + ",\"timeDiffGroup\":3600,\"resizeWidth\":1000,\"resizeHeight\":1000}";

		this.mvc.perform(put(requestUrl).content(requestJson).header("Authorization", SecurityTestUtil.createBearerTokenAdminUser())
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(expectedStatus))
				.andExpect(content().json(expectedJson));
	}
	
	@Test
	void testUpdateProcessingParametersDefaultBadRequest400() throws Exception {
		String requestUrl = "/admin/parameters-default";
		String requestJson = "{\"id\":9999,\"timeDiffGroup\":3600,\"resizeWidth\":1000,\"resizeHeight\":1000}";
		int expectedStatus = 400;
		String expectedJson = "";

		this.mvc.perform(put(requestUrl).content(requestJson).header("Authorization", SecurityTestUtil.createBearerTokenAdminUser())
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
	}	
	
}