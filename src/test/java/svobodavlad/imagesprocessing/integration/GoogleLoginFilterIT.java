package svobodavlad.imagesprocessing.integration;

import java.security.GeneralSecurityException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.json.webtoken.JsonWebSignature.Header;

import svobodavlad.imagesprocessing.security.User;
import svobodavlad.imagesprocessing.security.UserRepository;
import svobodavlad.imagesprocessing.testutil.IntegTestTemplate;
import svobodavlad.imagesprocessing.testutil.SecurityTestUtil;

class GoogleLoginFilterIT extends IntegTestTemplate {

	@Autowired
	private UserRepository userRepository;

	@MockBean
	private GoogleIdTokenVerifier googleIdTokenVerifier;
	
	@Autowired
	private SecurityTestUtil securityTestUtil;
	
	private User defaultUser;

	@BeforeEach
	void initData() {
		defaultUser = securityTestUtil.saveDefaultUserGoogle();
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
		payload.setSubject(defaultUser.getUsername());
		payload.set("given_name", defaultUser.getGivenName());
		payload.set("family_name", defaultUser.getFamilyName());
		GoogleIdToken idToken = new GoogleIdToken(header, payload, new byte[0], new byte[0]);
		this.given(googleIdTokenVerifier.verify("abcdef")).willReturn(idToken);
		
		ResultActions mvcResult = this.mockMvcPerformPostNoAuthorization(requestUrl, requestJson);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
		this.mockMvcExpectHeaderExists(mvcResult, expectedHeader);
	}

	@Test
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
	}

}