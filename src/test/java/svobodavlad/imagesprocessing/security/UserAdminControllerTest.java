package svobodavlad.imagesprocessing.security;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

import svobodavlad.imagesprocessing.jpaentities.Role;
import svobodavlad.imagesprocessing.jpaentities.User;
import svobodavlad.imagesprocessing.testutil.UnitTestTemplateMockMvc;

@WebMvcTest(UserAdminController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserAdminControllerTest extends UnitTestTemplateMockMvc {
	
	private static final String MOCKED_USER_NAME = "user";
	private static final String MOCKED_USER_EMAIL = "user@example.com";	
	private static final String MOCKED_USER_ADMIN_NAME = "admin";
	private static final String MOCKED_USER_ADMIN_EMAIL = "admin@example.com";
	private static final String ROLE_USER = "ROLE_USER";
	private static final String ROLE_ADMIN = "ROLE_ADMIN";	
	
	@MockBean
	private UserRepository userRepository;
	
	@MockBean
	private UserService userService;
	
	@Test
	void getAllUsersOk200() throws Exception {
		String requestUrl = "/admin/users";
		int expectedStatus = 200;
		String expectedJson = "[{\"username\":\"user\",\"givenName\":\"user\",\"familyName\":\"user\",\"email\":\"user@example.com\",\"lastLoginDateTime\":null,\"previousLoginDateTime\":null,\"userRoles\":[{\"role\":{\"id\":0,\"name\":\"ROLE_USER\"}}]},"
				+ "{\"username\":\"admin\",\"givenName\":\"admin\",\"familyName\":\"admin\",\"email\":\"admin@example.com\",\"lastLoginDateTime\":null,\"previousLoginDateTime\":null,\"userRoles\":[{\"role\":{\"id\":0,\"name\":\"ROLE_USER\"}},{\"role\":{\"id\":0,\"name\":\"ROLE_ADMIN\"}}]}]";
		
		User mockedUser = new User(MOCKED_USER_NAME, MOCKED_USER_NAME, MOCKED_USER_NAME);
		mockedUser.setEmail(MOCKED_USER_EMAIL);
		mockedUser.addRole(new Role(ROLE_USER));
		User mockedUserAdmin = new User(MOCKED_USER_ADMIN_NAME, MOCKED_USER_ADMIN_NAME, MOCKED_USER_ADMIN_NAME);
		mockedUserAdmin.setEmail(MOCKED_USER_ADMIN_EMAIL);
		mockedUserAdmin.addRole(new Role(ROLE_USER));
		mockedUserAdmin.addRole(new Role(ROLE_ADMIN));
		this.given(userRepository.findAll()).willReturn(new ArrayList<User>(List.of(mockedUser, mockedUserAdmin)));
		
		ResultActions mvcResult = this.mockMvcPerformGetNoAuthorization(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}
	
}
