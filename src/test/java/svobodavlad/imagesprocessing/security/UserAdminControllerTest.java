package svobodavlad.imagesprocessing.security;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

import svobodavlad.imagesprocessing.jpaentities.User;
import svobodavlad.imagesprocessing.jpaentities.UserRoles;
import svobodavlad.imagesprocessing.testutil.SecurityMockUtil;
import svobodavlad.imagesprocessing.testutil.UnitTestTemplate;

class UserAdminControllerTest extends UnitTestTemplate {

	@MockBean
	private UserRepository userRepository;
	
	@MockBean
	private UserService userService;

	@Test
	void testGetAllUsersOk200() throws Exception {
		String requestUrl = "/admin/users";
		int expectedStatus = 200;
		String expectedJson = "[{\"username\":\"user1\",\"givenName\":\"User 1\",\"familyName\":\"User 1\",\"email\":\"user1@gmail.com\",\"lastLoginDateTime\":null,\"previousLoginDateTime\":null,\"userRoles\":[{\"role\":{\"id\":0,\"name\":\"ROLE_USER\"}}]},"
				+ "{\"username\":\"admin\",\"givenName\":\"Administrator\",\"familyName\":\"Administrator\",\"email\":\"admin@gmail.com\",\"lastLoginDateTime\":null,\"previousLoginDateTime\":null,\"userRoles\":[{\"role\":{\"id\":1,\"name\":\"ROLE_ADMIN\"}},{\"role\":{\"id\":0,\"name\":\"ROLE_USER\"}}]}]";

		this.given(userRepository.findAll()).willReturn(new ArrayList<User>(List.of(
				SecurityMockUtil.getMockedDefaultUserInternal(), 
				SecurityMockUtil.getMockedAdminUser())));
		
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
