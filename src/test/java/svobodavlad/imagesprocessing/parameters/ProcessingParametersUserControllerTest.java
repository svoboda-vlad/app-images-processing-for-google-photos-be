package svobodavlad.imagesprocessing.parameters;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import svobodavlad.imagesprocessing.security.User;
import svobodavlad.imagesprocessing.security.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
//@Transactional - removed due to false positive tests (error in production: detached entity passed to persist)
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
	
	private User mockedUser;

	@BeforeEach
	private void initData() {
		mockedUser = SecurityMockUtil.getMockedDefaultUser();
		given(userDetailsService.loadUserByUsername(mockedUser.getUsername())).willReturn(mockedUser);
	}

	@Test
	void testGetProcessingParametersUserOk200() throws Exception {
		String requestUrl = "/parameters";
		int expectedStatus = 200;
		String expectedJson = "{\"id\":1,\"timeDiffGroup\":1800,\"resizeWidth\":1000,\"resizeHeight\":1000}";
		
		ProcessingParametersUser parameters = new ProcessingParametersUser(1800, 1000, 1000, mockedUser);
		parameters.setId(1);
		
		given(userRepository.findByUsername(mockedUser.getUsername())).willReturn(Optional.of(mockedUser));
		given(parametersRepository.findByUser(mockedUser)).willReturn(Optional.of(parameters));

		this.mvc.perform(get(requestUrl).header("Authorization", SecurityTestUtil.createBearerTokenDefaultUser())
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().is(expectedStatus))
				.andExpect(content().json(expectedJson));		
	}
	
}