package svobodavlad.imagesprocessing.google;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.ResultActions;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.json.webtoken.JsonWebSignature.Header;

import svobodavlad.imagesprocessing.security.User;
import svobodavlad.imagesprocessing.security.UserService;
import svobodavlad.imagesprocessing.testutil.SecurityMockUtil;
import svobodavlad.imagesprocessing.testutil.UnitTestTemplate;

class GoogleLoginFilterTest extends UnitTestTemplate {

	@Autowired
	private PasswordEncoder encoder;	
	
	@MockBean
	private GoogleIdTokenVerifier googleIdTokenVerifier;

	@MockBean
	private UserService userService;
	
	@MockBean
	private UserDetailsService userDetailsService;	
	
	private User mockedUser;

	@BeforeEach
	private void initData() {
		mockedUser = SecurityMockUtil.getMockedDefaultUserGoogle();
		mockedUser.setPassword(encoder.encode(mockedUser.getUsername()));
		this.given(userDetailsService.loadUserByUsername(mockedUser.getUsername())).willReturn(mockedUser);
	}

	@Test
	void testGoogleLoginOk200() throws Exception {
		String requestUrl = "/google-login";
		String requestJson = "{\"idToken\":\"abcdef\"}";
		int expectedStatus = 200;
		String expectedJson = "";
		String expectedHeader = "Authorization";

		Header header = new Header();
		Payload payload = new Payload();
		payload.setSubject(mockedUser.getUsername());
		payload.set("given_name", mockedUser.getGivenName());
		payload.set("family_name", mockedUser.getFamilyName());
		GoogleIdToken idToken = new GoogleIdToken(header, payload, new byte[0], new byte[0]);
		this.given(googleIdTokenVerifier.verify("abcdef")).willReturn(idToken);
		
		ResultActions mvcResult = this.mockMvcPerformPostNoAuthorization(requestUrl, requestJson);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
		this.mockMvcExpectHeaderExists(mvcResult, expectedHeader);
				
		this.verify(userService, this.times(1)).updateLastLoginDateTime(mockedUser.getUsername());
	}

	/*@Test
	void testGoogleLoginRegisterNewUserOk200() throws Exception {
		String requestUrl = "/google-login";
		String requestJson = "{\"idToken\":\"abcdef\"}";
		int expectedStatus = 200;
		String expectedJson = "";
		String expectedHeader = "Authorization";
		String newUserUsername = "user322";

		Header header = new Header();
		Payload payload = new Payload();
		payload.setSubject(newUserUsername);
		payload.set("given_name", "User 322");
		payload.set("family_name", "User 322");
		GoogleIdToken idToken = new GoogleIdToken(header, payload, new byte[0], new byte[0]);
		this.given(googleIdTokenVerifier.verify("abcdef")).willReturn(idToken);

		ResultActions mvcResult = this.mockMvcPerformPostNoAuthorization(requestUrl, requestJson);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
		this.mockMvcExpectHeaderExists(mvcResult, expectedHeader);

		requestUrl = "/user";
		expectedStatus = 200;
		Optional<User> optUser = userRepository.findByUsername(newUserUsername);
		expectedJson = "{\"username\":\"user322\",\"givenName\":\"User 322\",\"familyName\":\"User 322\",\"userRoles\":[{\"role\":{\"name\":\"ROLE_USER\"}}],\"lastLoginDateTime\":\""
				+ optUser.get().getLastLoginDateTime() + "\",\"previousLoginDateTime\":\""
				+ optUser.get().getLastLoginDateTime() + "\"}";

		mvcResult = this.mockMvcPerformGetAuthorizationForUsername(requestUrl, newUserUsername);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}

	@Test
	void testGoogleLoginVerificationErrorUnauthorized401() throws Exception {
		String requestUrl = "/google-login";
		String requestJson = "{\"idToken\":\"abcdef\"}";
		int expectedStatus = 401;
		String expectedJson = "";
		String unexpectedHeader = "Authorization";

		this.given(googleIdTokenVerifier.verify("abcdef")).willThrow(new GeneralSecurityException());
		
		ResultActions mvcResult = this.mockMvcPerformPostNoAuthorization(requestUrl, requestJson);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
		this.mockMvcExpectHeaderDoesNotExist(mvcResult, unexpectedHeader);		
	}

	@Test
	void testGoogleLoginInvalidJsonUnauthorized401() throws Exception {
		String requestUrl = "/google-login";
		String requestJson = "{\"idTokenx\":\"abcdef\"}";
		int expectedStatus = 401;
		String expectedJson = "";
		String unexpectedHeader = "Authorization";
		
		ResultActions mvcResult = this.mockMvcPerformPostNoAuthorization(requestUrl, requestJson);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
		this.mockMvcExpectHeaderDoesNotExist(mvcResult, unexpectedHeader);		
	}

	@Test
	void testGoogleLoginInvalidTokenUnauthorized401() throws Exception {
		String requestUrl = "/google-login";
		String requestJson = "{\"idToken\":\"abcdef\"}";
		int expectedStatus = 401;
		String expectedJson = "";
		String unexpectedHeader = "Authorization";

		this.given(googleIdTokenVerifier.verify("abcdef")).willReturn(null);

		ResultActions mvcResult = this.mockMvcPerformPostNoAuthorization(requestUrl, requestJson);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
		this.mockMvcExpectHeaderDoesNotExist(mvcResult, unexpectedHeader);
	}*/

}
