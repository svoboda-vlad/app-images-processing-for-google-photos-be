package svobodavlad.imagesprocessing.parametersdefault;

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

import svobodavlad.imagesprocessing.integration.SecurityMockUtil;
import svobodavlad.imagesprocessing.integration.SecurityTestUtil;

@SpringBootTest
@AutoConfigureMockMvc
//@Transactional - removed due to false positive tests (error in production: detached entity passed to persist)
//@WithMockUser - not needed
public class ProcessingParametersDefaultControllerTest {

	@Autowired
	private MockMvc mvc;
	
	@MockBean
	private ProcessingParametersDefaultRepository parametersRepository;
	
	@MockBean
	private UserDetailsService userDetailsService;

	@BeforeEach
	private void initData() {
		given(userDetailsService.loadUserByUsername(SecurityMockUtil.getMockedAdminUser().getUsername())).willReturn(SecurityMockUtil.getMockedAdminUser());
	}

	@Test
	void testGetProcessingParametersDefaultOk200() throws Exception {
		String requestUrl = "/admin/parameters-default";
		int expectedStatus = 200;
		String expectedJson = "{\"id\":1,\"timeDiffGroup\":1800,\"resizeWidth\":1000,\"resizeHeight\":1000}";
		
		List<ProcessingParametersDefault> parametersList = new ArrayList<ProcessingParametersDefault>();
		ProcessingParametersDefault parameters = new ProcessingParametersDefault(1800, 1000, 1000);
		parameters.setId(1);
		parametersList.add(parameters);
		
		given(parametersRepository.findAll()).willReturn(parametersList);

		this.mvc.perform(get(requestUrl).header("Authorization", SecurityTestUtil.createBearerTokenAdminUser())
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().is(expectedStatus))
				.andExpect(content().json(expectedJson));		
	}
	
	@Test
	void testGetProcessingParametersDefaultNotFound404() throws Exception {
		String requestUrl = "/admin/parameters-default";
		int expectedStatus = 404;
		String expectedJson = "";
		
		given(parametersRepository.findAll()).willReturn(new ArrayList<ProcessingParametersDefault>());

		this.mvc.perform(get(requestUrl).header("Authorization", SecurityTestUtil.createBearerTokenAdminUser())
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));		
	}	
	
	@Test
	void testUpdateProcessingParametersDefaultOk200() throws Exception {
		String requestUrl = "/admin/parameters-default";
		String requestJson = "{\"id\":1,\"timeDiffGroup\":3600,\"resizeWidth\":1000,\"resizeHeight\":1000}";
		int expectedStatus = 200;
		String expectedJson = "{\"id\":1,\"timeDiffGroup\":3600,\"resizeWidth\":1000,\"resizeHeight\":1000}";
		
		ProcessingParametersDefault parameters = new ProcessingParametersDefault(3600, 1000, 1000);
		parameters.setId(1);
		given(parametersRepository.findById(1L)).willReturn(Optional.of(parameters));
		given(parametersRepository.save(parameters)).willReturn(parameters);

		this.mvc.perform(put(requestUrl).content(requestJson).header("Authorization", SecurityTestUtil.createBearerTokenAdminUser())
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(expectedStatus))
				.andExpect(content().json(expectedJson));		
	}	
	
	@Test
	void testUpdateProcessingParametersDefaultBadRequest400() throws Exception {
		String requestUrl = "/admin/parameters-default";
		String requestJson = "{\"id\":2,\"timeDiffGroup\":3600,\"resizeWidth\":1000,\"resizeHeight\":1000}";
		int expectedStatus = 400;
		String expectedJson = "";
		
		given(parametersRepository.findById(2L)).willReturn(Optional.empty());

		this.mvc.perform(put(requestUrl).content(requestJson).header("Authorization", SecurityTestUtil.createBearerTokenAdminUser())
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
	}
	
	@Test
	void testUpdateProcessingParametersDefaultNotFound404() throws Exception {
		String requestUrl = "/admin/parameters-default";
		String requestJson = "{\"timeDiffGroup\":3600,\"resizeWidth\":1000,\"resizeHeight\":1000}";
		int expectedStatus = 400;
		String expectedJson = "";
		
		this.mvc.perform(put(requestUrl).content(requestJson).header("Authorization", SecurityTestUtil.createBearerTokenAdminUser())
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
	}
	
}