package svobodavlad.imagesprocessing.security;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.ResultActions;

import svobodavlad.imagesprocessing.security.User.LoginProvider;
import svobodavlad.imagesprocessing.testutil.SecurityMockUtil;
import svobodavlad.imagesprocessing.testutil.UnitTestTemplate;

class UserAdminControllerTest extends UnitTestTemplate {

	@MockBean
	private UserRepository userRepository;

	@MockBean
	private UserDetailsService userDetailsService;

	private static final String ROLE_USER = "ROLE_USER";
	private static final String ROLE_ADMIN = "ROLE_ADMIN";

	@BeforeEach
	private void initData() {
		this.given(userDetailsService.loadUserByUsername(SecurityMockUtil.getMockedAdminUser().getUsername())).willReturn(SecurityMockUtil.getMockedAdminUser());
	}

	@Test
	void testgetAllUsersOk200() throws Exception {
		String requestUrl = "/admin/users";
		int expectedStatus = 200;
		String expectedJson = "[{\"username\":\"user1\",\"givenName\":\"User 1\",\"familyName\":\"User 1\",\"lastLoginDateTime\":null,\"previousLoginDateTime\":null,\"userRoles\":[{\"role\":{\"id\":0,\"name\":\"ROLE_USER\"}}]},"
				+ "{\"username\":\"user2\",\"givenName\":\"User 2\",\"familyName\":\"User 2\",\"lastLoginDateTime\":null,\"previousLoginDateTime\":null,\"userRoles\":[{\"role\":{\"id\":0,\"name\":\"ROLE_USER\"}},{\"role\":{\"id\":0,\"name\":\"ROLE_ADMIN\"}}]}]";

		User user1 = new User(0L, "user1", "A".repeat(60), LoginProvider.INTERNAL, "User 1", "User 1", null, null, new ArrayList<UserRoles>());
		user1.addRole(new Role(0L, ROLE_USER));
		User user2 = new User(0L, "user2", "A".repeat(60), LoginProvider.INTERNAL, "User 2", "User 2", null, null, new ArrayList<UserRoles>());
		user2.addRole(new Role(0L, ROLE_USER));
		user2.addRole(new Role(0L, ROLE_ADMIN));

		this.given(userRepository.findAll()).willReturn(new ArrayList<User>(List.of(user1, user2)));
		
		ResultActions mvcResult = this.mockMvcPerformGetAuthorizationAdminUser(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}

}
