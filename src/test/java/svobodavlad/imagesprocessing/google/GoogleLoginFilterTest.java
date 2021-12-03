package svobodavlad.imagesprocessing.google;

import java.security.GeneralSecurityException;
import java.util.HashSet;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.ResultActions;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.json.webtoken.JsonWebSignature.Header;

import svobodavlad.imagesprocessing.jpaentities.User;
import svobodavlad.imagesprocessing.jpaentities.UserRoles;
import svobodavlad.imagesprocessing.security.UserRepository;
import svobodavlad.imagesprocessing.security.UserService;
import svobodavlad.imagesprocessing.testutil.SecurityMockUtil;
import svobodavlad.imagesprocessing.testutil.UnitTestTemplateWithSecurity;

class GoogleLoginFilterTest extends UnitTestTemplateWithSecurity {

	@MockBean
	private PasswordEncoder encoder;	
	
	@MockBean
	private GoogleIdTokenVerifier googleIdTokenVerifier;

	@MockBean
	private UserService userService;
	
	@MockBean
	private UserRepository userRepository;	
	
	@MockBean
	private UserDetailsService userDetailsService;	
	
	private User mockedUser;

	@BeforeEach
	private void initData() {
		mockedUser = SecurityMockUtil.getMockedDefaultUserGoogle();
		this.given(encoder.encode(mockedUser.getUsername())).willReturn(mockedUser.getPassword());
		this.given(encoder.matches(mockedUser.getUsername(),mockedUser.getPassword())).willReturn(true);		
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
		this.given(userRepository.findByUsername(mockedUser.getUsername())).willReturn(Optional.of(mockedUser));
		
		ResultActions mvcResult = this.mockMvcPerformPostNoAuthorization(requestUrl, requestJson);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
		this.mockMvcExpectHeaderExists(mvcResult, expectedHeader);
		
		User mockedUserWithoutRoles = SecurityMockUtil.getMockedDefaultUserGoogle();
		mockedUserWithoutRoles.setRoles(new HashSet<UserRoles>());
		
		this.verify(userService, this.never()).registerUser(mockedUserWithoutRoles);
		this.verify(userService, this.times(1)).updateLastLoginDateTime(mockedUser.getUsername());
	}
	
	@Test
	void testGoogleLoginUserFoundButInternalUnauthorized401() throws Exception {
		String requestUrl = "/google-login";
		String requestJson = "{\"idToken\":\"abcdef\"}";
		int expectedStatus = 401;
		String expectedJson = "";
		String unexpectedHeader = "Authorization";

		Header header = new Header();
		Payload payload = new Payload();
		payload.setSubject(mockedUser.getUsername());
		payload.set("given_name", mockedUser.getGivenName());
		payload.set("family_name", mockedUser.getFamilyName());
		GoogleIdToken idToken = new GoogleIdToken(header, payload, new byte[0], new byte[0]);
		this.given(googleIdTokenVerifier.verify("abcdef")).willReturn(idToken);
		User mockedUserInternal = SecurityMockUtil.getMockedDefaultUserInternal();
		this.given(userRepository.findByUsername(mockedUser.getUsername())).willReturn(Optional.of(mockedUserInternal));
		
		ResultActions mvcResult = this.mockMvcPerformPostNoAuthorization(requestUrl, requestJson);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
		this.mockMvcExpectHeaderDoesNotExist(mvcResult, unexpectedHeader);
		
		User mockedUserWithoutRoles = SecurityMockUtil.getMockedDefaultUserGoogle();
		mockedUserWithoutRoles.setRoles(new HashSet<UserRoles>());
		
		this.verify(userService, this.never()).registerUser(mockedUserWithoutRoles);
		this.verify(userService, this.never()).updateLastLoginDateTime(mockedUser.getUsername());
	}

	@Test
	void testGoogleLoginRegisterNewUserOk200() throws Exception {
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
		this.given(userRepository.findByUsername(mockedUser.getUsername())).willReturn(Optional.empty());
		
		ResultActions mvcResult = this.mockMvcPerformPostNoAuthorization(requestUrl, requestJson);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
		this.mockMvcExpectHeaderExists(mvcResult, expectedHeader);
		
		User mockedUserWithoutRoles = SecurityMockUtil.getMockedDefaultUserGoogle();
		mockedUserWithoutRoles.setRoles(new HashSet<UserRoles>());
		
		this.verify(userService, this.times(1)).registerUser(mockedUserWithoutRoles);
		this.verify(userService, this.times(1)).updateLastLoginDateTime(mockedUser.getUsername());
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
		
		User mockedUserWithoutRoles = SecurityMockUtil.getMockedDefaultUserGoogle();
		mockedUserWithoutRoles.setRoles(new HashSet<UserRoles>());
		
		this.verify(userService, this.never()).registerUser(mockedUserWithoutRoles);
		this.verify(userService, this.never()).updateLastLoginDateTime(mockedUser.getUsername());
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
		
		this.verify(googleIdTokenVerifier, this.never()).verify("abcdef");
		
		User mockedUserWithoutRoles = SecurityMockUtil.getMockedDefaultUserGoogle();
		mockedUserWithoutRoles.setRoles(new HashSet<UserRoles>());
		
		this.verify(userService, this.never()).registerUser(mockedUserWithoutRoles);
		this.verify(userService, this.never()).updateLastLoginDateTime(mockedUser.getUsername());
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
		
		User mockedUserWithoutRoles = SecurityMockUtil.getMockedDefaultUserGoogle();
		mockedUserWithoutRoles.setRoles(new HashSet<UserRoles>());
		
		this.verify(userService, this.never()).registerUser(mockedUserWithoutRoles);
		this.verify(userService, this.never()).updateLastLoginDateTime(mockedUser.getUsername());		
	}

}
