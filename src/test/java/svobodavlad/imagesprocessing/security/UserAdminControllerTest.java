package svobodavlad.imagesprocessing.security;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

import svobodavlad.imagesprocessing.jpaentities.User;
import svobodavlad.imagesprocessing.testutil.UnitTestTemplateMockMvc;

@WebMvcTest(UserAdminController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserAdminControllerTest extends UnitTestTemplateMockMvc {
	
	private static final String MOCKED_USER_NAME = "user";
	private static final String MOCKED_USER_EMAIL = "user@example.com";	
	private static final String MOCKED_USER_ADMIN_NAME = "admin";
	private static final String MOCKED_USER_ADMIN_EMAIL = "admin@example.com";
	
	@MockBean
	private UserRepository userRepository;
	
	@MockBean
	private UserService userService;
	
	@Test
	void getAllUsersOk200() throws Exception {
		String requestUrl = "/admin/users";
		int expectedStatus = 200;
		String expectedJson = "[{\"username\":\"user\",\"givenName\":\"user\",\"familyName\":\"user\",\"email\":\"user@example.com\"},"
				+ "{\"username\":\"admin\",\"givenName\":\"admin\",\"familyName\":\"admin\",\"email\":\"admin@example.com\"}]";
		
		User mockedUser = new User().setUsername(MOCKED_USER_NAME).setGivenName(MOCKED_USER_NAME).setFamilyName(MOCKED_USER_NAME);
		mockedUser.setEmail(MOCKED_USER_EMAIL);
		User mockedUserAdmin = new User().setUsername(MOCKED_USER_ADMIN_NAME).setGivenName(MOCKED_USER_ADMIN_NAME).setFamilyName(MOCKED_USER_ADMIN_NAME);
		mockedUserAdmin.setEmail(MOCKED_USER_ADMIN_EMAIL);
		this.given(userRepository.findAll()).willReturn(new ArrayList<User>(List.of(mockedUser, mockedUserAdmin)));
		
		ResultActions mvcResult = this.mockMvcPerformGetNoAuthorization(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}
	
}
