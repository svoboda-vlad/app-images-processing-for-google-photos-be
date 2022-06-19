package svobodavlad.imagesprocessing.security;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

import svobodavlad.imagesprocessing.jpaentities.User;
import svobodavlad.imagesprocessing.testutil.UnitTestTemplateMockMvc;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest extends UnitTestTemplateMockMvc {
	
	private static final String MOCKED_USER_NAME = "user";
	private static final String MOCKED_USER_EMAIL = "user@example.com";

	@MockBean
	private UserRepository userRepository;

	@MockBean
	private UserService userService;
	
	@Test
	void getUserInfoOk200() throws Exception {
		String requestUrl = "/user";
		int expectedStatus = 200;
		String expectedJson = "{\"username\":\"user\",\"givenName\":\"user\",\"familyName\":\"user\",\"email\":\"user@example.com\",\"lastLoginDateTime\":null,\"previousLoginDateTime\":null}";

		User mockedUser = new User(MOCKED_USER_NAME, MOCKED_USER_NAME, MOCKED_USER_NAME);
		mockedUser.setEmail(MOCKED_USER_EMAIL);
		this.given(userService.getCurrentUser()).willReturn(Optional.of(mockedUser));

		ResultActions mvcResult = this.mockMvcPerformGetNoAuthorization(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}

	@Test
	void deleteUserNoContent204() throws Exception {
		String requestUrl = "/user";
		int expectedStatus = 204;
		String expectedJson = "";

		User mockedUser = new User(MOCKED_USER_NAME, MOCKED_USER_NAME, MOCKED_USER_NAME);
		mockedUser.setId(1L);
		
		ResultActions mvcResult = this.mockMvcPerformDeleteNoAuthorization(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);

		this.verify(userService, this.times(1)).deleteCurrentUser();
	}

}
