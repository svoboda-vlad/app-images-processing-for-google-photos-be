package svobodavlad.imagesprocessing.integration;

import java.security.GeneralSecurityException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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

import svobodavlad.imagesprocessing.jpaentities.User;
import svobodavlad.imagesprocessing.jpaentities.UserRoles;
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
		payload.setEmail(defaultUser.getEmail());
		GoogleIdToken idToken = new GoogleIdToken(header, payload, new byte[0], new byte[0]);
		this.given(googleIdTokenVerifier.verify("abcdef")).willReturn(idToken);
		
		ResultActions mvcResult = this.mockMvcPerformPostNoAuthorization(requestUrl, requestJson);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
		this.mockMvcExpectHeaderExists(mvcResult, expectedHeader);
	}
	
	@Test
	void testGoogleLoginUserFoundButInternalUnauthorized401() throws Exception {
		String requestUrl = "/google-login";
		String requestJson = "{\"idToken\":\"abcdef\"}";
		int expectedStatus = 401;
		String expectedJson = "";
		String unexpectedHeader = "Authorization";
		
		User defaultUserInternal = securityTestUtil.saveDefaultUserInternal();

		Header header = new Header();
		Payload payload = new Payload();
		payload.setSubject(defaultUserInternal.getUsername());
		GoogleIdToken idToken = new GoogleIdToken(header, payload, new byte[0], new byte[0]);
		this.given(googleIdTokenVerifier.verify("abcdef")).willReturn(idToken);
		
		ResultActions mvcResult = this.mockMvcPerformPostNoAuthorization(requestUrl, requestJson);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
		this.mockMvcExpectHeaderDoesNotExist(mvcResult, unexpectedHeader);
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
		payload.setEmail("user322@gmail.com");
		GoogleIdToken idToken = new GoogleIdToken(header, payload, new byte[0], new byte[0]);
		this.given(googleIdTokenVerifier.verify("abcdef")).willReturn(idToken);

		ResultActions mvcResult = this.mockMvcPerformPostNoAuthorization(requestUrl, requestJson);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
		this.mockMvcExpectHeaderExists(mvcResult, expectedHeader);
		
		DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.systemDefault());

		requestUrl = "/user";
		expectedStatus = 200;
		Optional<User> optUser = userRepository.findByUsername(newUserUsername);
		String rolesJson = "";
		for (UserRoles userRole : optUser.get().getRoles()) {
			if (rolesJson.length() > 0) rolesJson += ",";
			rolesJson += "{\"role\":{\"id\":" + userRole.getRole().getId() +",\"name\":\"" + userRole.getRole().getName() + "\"}}";
		}
		expectedJson = "{\"username\":\"user322\",\"givenName\":\"User 322\",\"familyName\":\"User 322\",\"userRoles\":[" 
				+ rolesJson + "],\"lastLoginDateTime\":\""
				+ formatter.format(optUser.get().getLastLoginDateTime()) + "\",\"previousLoginDateTime\":\""
				+ formatter.format(optUser.get().getLastLoginDateTime()) + "\",\"email\":\"user322@gmail.com\"}";

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
	
	@Test
	void testGoogleLoginOk200UserInfoUpdated() throws Exception {
		String requestUrl = "/google-login";
		String requestJson = "{\"idToken\":\"abcdef\"}";
		int expectedStatus = 200;
		String expectedJson = "";
		String expectedHeader = "Authorization";

		Header header = new Header();
		Payload payload = new Payload();
		payload.setSubject(defaultUser.getUsername());
		payload.set("given_name", defaultUser.getGivenName() + "x");
		payload.set("family_name", defaultUser.getFamilyName() + "x");
		payload.setEmail(defaultUser.getEmail() + "x");
		GoogleIdToken idToken = new GoogleIdToken(header, payload, new byte[0], new byte[0]);
		this.given(googleIdTokenVerifier.verify("abcdef")).willReturn(idToken);
		
		ResultActions mvcResult = this.mockMvcPerformPostNoAuthorization(requestUrl, requestJson);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
		this.mockMvcExpectHeaderExists(mvcResult, expectedHeader);
		
		DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.systemDefault());
		
		requestUrl = "/user";
		expectedStatus = 200;
		String rolesJson = "";
		for (UserRoles userRole : defaultUser.getRoles()) {
			if (rolesJson.length() > 0) rolesJson += ",";
			rolesJson += "{\"role\":{\"id\":" + userRole.getRole().getId() +",\"name\":\"" + userRole.getRole().getName() + "\"}}";
		}
		expectedJson = "{\"username\":\"usergoogle1\",\"givenName\":\"User 1x\",\"familyName\":\"User 1x\",\"email\":\"user1@gmail.comx\",\"userRoles\":[" + rolesJson + "],\"lastLoginDateTime\":\""
				+ formatter.format(defaultUser.getLastLoginDateTime()) + "\",\"previousLoginDateTime\":\""
				+ formatter.format(defaultUser.getLastLoginDateTime()) + "\"}";

		mvcResult = this.mockMvcPerformGetAuthorizationDefaultUserGoogle(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
		
	}	

}