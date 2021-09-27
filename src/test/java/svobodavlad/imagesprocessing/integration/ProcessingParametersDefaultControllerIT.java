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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import svobodavlad.imagesprocessing.parameters.ProcessingParametersDefault;
import svobodavlad.imagesprocessing.parameters.ProcessingParametersDefaultRepository;
import svobodavlad.imagesprocessing.testutil.SecurityTestUtil;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("h2")
//@WithMockUser - not needed
public class ProcessingParametersDefaultControllerIT {

	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private SecurityTestUtil securityTestUtil;
	
	@Autowired
	private ProcessingParametersDefaultRepository parametersRepository;
	
	@BeforeEach
	void initData() {
		securityTestUtil.saveAdminUser();
		parametersRepository.save(new ProcessingParametersDefault(0L, 1800, 1000, 1000));
	}

	@Test
	void testGetProcessingParametersDefaultTemplateOk200() throws Exception {		
		String requestUrl = "/admin/parameters-default";
		int expectedStatus = 200;
		String expectedJson = "{\"timeDiffGroup\":1800,\"resizeWidth\":1000,\"resizeHeight\":1000}";

		this.mvc.perform(get(requestUrl).header("Authorization", SecurityTestUtil.createBearerTokenAdminUser())
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().is(expectedStatus))
				.andExpect(content().json(expectedJson));		
	}
	
	@Test
	void testGetProcessingParametersDefaultTemplateNotFound404() throws Exception {
		String requestUrl = "/admin/parameters-default";
		int expectedStatus = 404;
		String expectedJson = "";
		
		parametersRepository.deleteAll();		

		this.mvc.perform(get(requestUrl).header("Authorization", SecurityTestUtil.createBearerTokenAdminUser())
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));		
	}	

	@Test
	void testUpdateProcessingParametersDefaultTemplateOk200() throws Exception {
		String requestUrl = "/admin/parameters-default";
		String requestJson = "{\"timeDiffGroup\":3600,\"resizeWidth\":1000,\"resizeHeight\":1000}";
		int expectedStatus = 200;
		String expectedJson = "{\"timeDiffGroup\":3600,\"resizeWidth\":1000,\"resizeHeight\":1000}";

		this.mvc.perform(put(requestUrl).content(requestJson).header("Authorization", SecurityTestUtil.createBearerTokenAdminUser())
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(expectedStatus))
				.andExpect(content().json(expectedJson));
	}
	
	@Test
	void testUpdateProcessingParametersDefaultTemplateNotFound404() throws Exception {
		String requestUrl = "/admin/parameters-default";
		String requestJson = "{\"timeDiffGroup\":3600,\"resizeWidth\":1000,\"resizeHeight\":1000}";
		int expectedStatus = 404;
		String expectedJson = "";
		
		parametersRepository.deleteAll();

		this.mvc.perform(put(requestUrl).content(requestJson).header("Authorization", SecurityTestUtil.createBearerTokenAdminUser())
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
	}	
}