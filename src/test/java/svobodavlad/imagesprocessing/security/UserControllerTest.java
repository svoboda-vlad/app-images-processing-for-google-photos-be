package svobodavlad.imagesprocessing.security;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.ResultActions;

import svobodavlad.imagesprocessing.jpaentities.Role;
import svobodavlad.imagesprocessing.jpaentities.User;
import svobodavlad.imagesprocessing.testutil.UnitTestTemplate;

class UserControllerTest extends UnitTestTemplate {
	
	private static final String MOCKED_USER_NAME = "user";
	private static final String MOCKED_USER_EMAIL = "user@example.com";
	private static final String ROLE_USER = "ROLE_USER";

	@MockBean
	private UserRepository userRepository;

	@MockBean
	private UserService userService;

	@MockBean
	private PasswordEncoder encoder;

	@Test
	void testGetCurrentUserOk200() throws Exception {
		String requestUrl = "/user";
		int expectedStatus = 200;
		String expectedJson = "{\"username\":\"user\",\"givenName\":\"user\",\"familyName\":\"user\",\"email\":\"user@example.com\",\"userRoles\":[{\"role\":{\"id\":0,\"name\":\"ROLE_USER\"}}],\"lastLoginDateTime\":null,\"previousLoginDateTime\":null}";

		User mockedUser = new UserRegister(MOCKED_USER_NAME, MOCKED_USER_NAME, MOCKED_USER_NAME, MOCKED_USER_EMAIL).toUser();
		mockedUser.addRole(new Role(ROLE_USER));
		this.given(userService.getCurrentUser()).willReturn(Optional.of(mockedUser));

		ResultActions mvcResult = this.mockMvcPerformGetNoAuthorization(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}

	@Test
	void testDeleteUserNoContent204() throws Exception {
		String requestUrl = "/user";
		int expectedStatus = 204;
		String expectedJson = "";

		User mockedUser = new UserRegister(MOCKED_USER_NAME, MOCKED_USER_NAME, MOCKED_USER_NAME, null).toUser();
		mockedUser.setId(1L);
		
		ResultActions mvcResult = this.mockMvcPerformDeleteNoAuthorization(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);

		this.verify(userService, this.times(1)).deleteCurrentUser();
	}

}
