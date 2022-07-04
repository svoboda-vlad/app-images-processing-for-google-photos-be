package svobodavlad.imagesprocessing.security;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;

import svobodavlad.imagesprocessing.jpaentities.User;
import svobodavlad.imagesprocessing.testutil.UnitTestTemplateMockMvc;

@WebMvcTest(UserAdminController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserAdminControllerTest extends UnitTestTemplateMockMvc {
	
	private static final String MOCKED_USER_NAME = "user";
	private static final String MOCKED_USER_EMAIL = "user@example.com";	
	private static final String MOCKED_USER_ADMIN_NAME = "admin";
	private static final String MOCKED_USER_ADMIN_EMAIL = "admin@example.com";
	
	private static final String ADMIN_USERS_URL = "/admin/users";
	
	private static final int HTTP_OK = 200;
	
	@MockBean
	private UserRepository userRepository;
	
	@MockBean
	private UserService userService;
	
    @Autowired
    private JacksonTester<List<UserTemplate>> jacksonTester;
	
	@Test
	void getAllUsersOk200() throws Exception {		
		var mockedUser = new User().setUsername(MOCKED_USER_NAME).setGivenName(MOCKED_USER_NAME).setFamilyName(MOCKED_USER_NAME)
				.setEmail(MOCKED_USER_EMAIL);
		var mockedUserAdmin = new User().setUsername(MOCKED_USER_ADMIN_NAME).setGivenName(MOCKED_USER_ADMIN_NAME).setFamilyName(MOCKED_USER_ADMIN_NAME)
				.setEmail(MOCKED_USER_ADMIN_EMAIL);
		given(userRepository.findAll()).willReturn(new ArrayList<User>(List.of(mockedUser, mockedUserAdmin)));
		var userTemplateList = List.of(mockedUser.toUserTemplate(), mockedUserAdmin.toUserTemplate());
		var expectedJson = jacksonTester.write(userTemplateList).getJson();
		
		var mvcResult = mockMvcPerformGetNoAuthorization(ADMIN_USERS_URL);
		mockMvcExpectStatusAndContent(mvcResult, HTTP_OK, expectedJson);
	}
	
}
