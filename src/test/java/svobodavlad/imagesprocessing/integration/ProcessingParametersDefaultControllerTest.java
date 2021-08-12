package svobodavlad.imagesprocessing.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import svobodavlad.imagesprocessing.parametersdefault.ProcessingParametersDefault;
import svobodavlad.imagesprocessing.parametersdefault.ProcessingParametersDefaultRepository;
import svobodavlad.imagesprocessing.security.AuthenticationService;
import svobodavlad.imagesprocessing.security.Role;
import svobodavlad.imagesprocessing.security.RoleRepository;
import svobodavlad.imagesprocessing.security.User;
import svobodavlad.imagesprocessing.security.User.LoginProvider;
import svobodavlad.imagesprocessing.security.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
//@Transactional - removed due to false positive tests (error in production: detached entity passed to persist)
//@WithMockUser - not needed
public class ProcessingParametersDefaultControllerTest {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private PasswordEncoder encoder;

	@Autowired
	private ProcessingParametersDefaultRepository parametersRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	private static final String USERNAME = "user1";
	private static final String PASSWORD = "pass123";
	private static final String ROLE_USER = "ROLE_USER";
	private static final String ROLE_ADMIN = "ROLE_ADMIN";	

	private String generateAuthorizationHeader(String username) {
		return "Bearer " + AuthenticationService.generateToken(username);
	}

	@BeforeEach
	void initData() {
		ProcessingParametersDefault parameters = new ProcessingParametersDefault(1800, 1000, 1000);
		parametersRepository.save(parameters);

		User user = new User(USERNAME, encoder.encode(PASSWORD), LoginProvider.INTERNAL, "User 1", "User 1");
		user = userRepository.save(user);
		Optional<Role> optRole1 = roleRepository.findByName(ROLE_USER);
		Optional<Role> optRole2 = roleRepository.findByName(ROLE_ADMIN);
		user.addRole(optRole1.get());
		user.addRole(optRole2.get());
		userRepository.save(user);
	}

	@AfterEach
	void cleanData() {
		parametersRepository.deleteAll();
		userRepository.deleteAll();
	}

	@Test
	void testGetCurrentUserOk200() throws Exception {
		String requestUrl = "/admin/parameters-default";
		int expectedStatus = 200;
		String expectedJson = "{\"id\":1,\"timeDiffGroup\":1800,\"resizeWidth\":1000,\"resizeHeight\":1000}";

		this.mvc.perform(get(requestUrl).header("Authorization", generateAuthorizationHeader(USERNAME))
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().is(expectedStatus))
				.andExpect(content().json(expectedJson));
	}

}