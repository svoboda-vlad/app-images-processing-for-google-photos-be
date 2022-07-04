package svobodavlad.imagesprocessing.security;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;

import svobodavlad.imagesprocessing.jpaentities.User;
import svobodavlad.imagesprocessing.testutil.UnitTestTemplateMockMvc;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest extends UnitTestTemplateMockMvc {
	
	private static final String MOCKED_USER_NAME = "user";
	private static final String MOCKED_USER_EMAIL = "user@example.com";
	private static final String USER_URL = "/user";

	private static final int HTTP_OK = 200;
	private static final int HTTP_NO_CONTENT = 204;

	@MockBean
	private UserRepository userRepository;

	@MockBean
	private UserService userService;
	
    @Autowired
    private JacksonTester<UserTemplate> jacksonTester;	
	
	@Test
	void getUserTemplateOk200() throws Exception {
		var mockedUser = new User().setUsername(MOCKED_USER_NAME).setGivenName(MOCKED_USER_NAME).setFamilyName(MOCKED_USER_NAME)
				.setEmail(MOCKED_USER_EMAIL);
		this.given(userService.getCurrentUser()).willReturn(Optional.of(mockedUser));
		var expectedJson = jacksonTester.write(mockedUser.toUserTemplate()).getJson();
		
		var mvcResult = this.mockMvcPerformGetNoAuthorization(USER_URL);
		this.mockMvcExpectStatusAndContent(mvcResult, HTTP_OK, expectedJson);
	}

	@Test
	void deleteUserNoContent204() throws Exception {
		var mockedUser = new User().setUsername(MOCKED_USER_NAME).setGivenName(MOCKED_USER_NAME).setFamilyName(MOCKED_USER_NAME);
		mockedUser.setId(1L);
		
		var mvcResult = this.mockMvcPerformDeleteNoAuthorization(USER_URL);
		this.mockMvcExpectStatusAndContent(mvcResult, HTTP_NO_CONTENT, "");
		this.verify(userService, this.times(1)).deleteCurrentUser();
	}

}
