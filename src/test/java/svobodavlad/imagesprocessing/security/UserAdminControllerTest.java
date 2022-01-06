package svobodavlad.imagesprocessing.security;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

import svobodavlad.imagesprocessing.jpaentities.Role;
import svobodavlad.imagesprocessing.jpaentities.User;
import svobodavlad.imagesprocessing.jpaentities.User.LoginProvider;
import svobodavlad.imagesprocessing.jpaentities.UserRoles;
import svobodavlad.imagesprocessing.testutil.SecurityMockUtil;
import svobodavlad.imagesprocessing.testutil.UnitTestTemplate;

class UserAdminControllerTest extends UnitTestTemplate {

	@MockBean
	private UserRepository userRepository;
	
	@MockBean
	private UserService userService;	

	private static final String ROLE_USER = "ROLE_USER";
	private static final String ROLE_ADMIN = "ROLE_ADMIN";

	@Test
	void testGetAllUsersOk200() throws Exception {
		String requestUrl = "/admin/users";
		int expectedStatus = 200;
		String expectedJson = "[{\"username\":\"user1\",\"givenName\":\"User 1\",\"familyName\":\"User 1\",\"email\":\"user1@gmail.com\",\"lastLoginDateTime\":null,\"previousLoginDateTime\":null,\"userRoles\":[{\"role\":{\"id\":0,\"name\":\"ROLE_USER\"}}]},"
				+ "{\"username\":\"admin\",\"givenName\":\"Admin\",\"familyName\":\"Admin\",\"email\":\"admin@gmail.com\",\"lastLoginDateTime\":null,\"previousLoginDateTime\":null,\"userRoles\":[{\"role\":{\"id\":0,\"name\":\"ROLE_USER\"}},{\"role\":{\"id\":0,\"name\":\"ROLE_ADMIN\"}}]}]";

		User user1 = new User("user1", "A".repeat(60), LoginProvider.INTERNAL, "User 1", "User 1");
		user1.addRole(new Role(ROLE_USER));
		user1.setEmail("user1@gmail.com");
		User user2 = new User("admin", "A".repeat(60), LoginProvider.INTERNAL, "Admin", "Admin");
		user2.addRole(new Role(ROLE_USER));
		user2.addRole(new Role(ROLE_ADMIN));
		user2.setEmail("admin@gmail.com");

		this.given(userRepository.findAll()).willReturn(new ArrayList<User>(List.of(user1, user2)));
		
		ResultActions mvcResult = this.mockMvcPerformGetNoAuthorization(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}
	
	@Test
	void testUpdateUserOk200() throws Exception {
		String requestUrl = "/admin/user";
		String requestJson = "{\"username\":\"user1\",\"lastLoginDateTime\":null,\"previousLoginDateTime\":null, \"givenName\": \"User X\",\"familyName\": \"User Y\",\"email\": \"userx@gmail.com\"}";
		int expectedStatus = 200;
		String expectedJson = "{\"username\":\"user1\",\"givenName\":\"User X\",\"familyName\":\"User Y\",\"email\": \"userx@gmail.com\",\"userRoles\":[{\"role\":{\"id\":0,\"name\":\"ROLE_USER\"}}],\"lastLoginDateTime\":null,\"previousLoginDateTime\":null}";

		UserInfo userInfo = new UserInfo(SecurityMockUtil.getMockedDefaultUserInternal().getUsername(), "User X", "User Y", "userx@gmail.com", null, null, new HashSet<UserRoles>());

		this.given(userService.updateCurrentUser(userInfo)).willReturn(userInfo.toUser(SecurityMockUtil.getMockedDefaultUserInternal()));
		
		ResultActions mvcResult = this.mockMvcPerformPutNoAuthorization(requestUrl, requestJson);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}

	@Test
	void testUpdateUserUsernameDoesNotMatchBadRequest400() throws Exception {
		String requestUrl = "/admin/user";
		String requestJson = "{\"username\":\"userx\",\"lastLoginDateTime\":null,\"previousLoginDateTime\":null, \"givenName\": \"User X\",\"familyName\": \"User Y\",\"email\": \"userx@gmail.com\"}";
		int expectedStatus = 400;
		String expectedJson = "";

		ResultActions mvcResult = this.mockMvcPerformPutNoAuthorization(requestUrl, requestJson);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}	

}
