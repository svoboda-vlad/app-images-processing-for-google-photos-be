package svobodavlad.imagesprocessing.integration;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.security.GeneralSecurityException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.json.webtoken.JsonWebSignature.Header;

import svobodavlad.imagesprocessing.security.AuthenticationService;
import svobodavlad.imagesprocessing.security.Role;
import svobodavlad.imagesprocessing.security.RoleRepository;
import svobodavlad.imagesprocessing.security.User;
import svobodavlad.imagesprocessing.security.User.LoginProvider;
import svobodavlad.imagesprocessing.security.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
//@WithMockUser - not needed
class GoogleLoginFilterIntegTest {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private PasswordEncoder encoder;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@MockBean
	private GoogleIdTokenVerifier googleIdTokenVerifier;

	@BeforeEach
	void initData() {
		User user = new User("user321", encoder.encode("user321"), LoginProvider.GOOGLE, "User 321", "User 321");
		Optional<Role> optRole = roleRepository.findByName("ROLE_USER");
		user = userRepository.save(user);
		user.addRole(optRole.get());
		userRepository.save(user);
	}

	@Test
	void testGoogleLoginOk200() throws Exception {
		String requestUrl = "/google-login";
		String requestJson = "{\"idToken\":\"abcdef\"}";
		int expectedStatus = 200;
		String expectedJson = "";

		Header header = new Header();
		Payload payload = new Payload();
		payload.setSubject("user321");
		payload.set("given_name", "User 321");
		payload.set("family_name", "User 321");
		GoogleIdToken idToken = new GoogleIdToken(header, payload, new byte[0], new byte[0]);
		given(googleIdTokenVerifier.verify("abcdef")).willReturn(idToken);

		this.mvc.perform(post(requestUrl).content(requestJson).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is(expectedStatus)).andExpect(content().string(expectedJson))
				.andExpect(header().exists("Authorization"));
	}

	@Test
	void testGoogleLoginRegisterNewUserOk200() throws Exception {
		String requestUrl = "/google-login";
		String requestJson = "{\"idToken\":\"abcdef\"}";
		int expectedStatus = 200;
		String expectedJson = "";

		Header header = new Header();
		Payload payload = new Payload();
		payload.setSubject("user322");
		payload.set("given_name", "User 322");
		payload.set("family_name", "User 322");
		GoogleIdToken idToken = new GoogleIdToken(header, payload, new byte[0], new byte[0]);
		given(googleIdTokenVerifier.verify("abcdef")).willReturn(idToken);

		this.mvc.perform(post(requestUrl).content(requestJson).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is(expectedStatus)).andExpect(content().string(expectedJson))
				.andExpect(header().exists("Authorization"));

		requestUrl = "/user";
		expectedStatus = 200;
		Optional<User> optUser = userRepository.findByUsername("user322");
		expectedJson = "{\"username\":\"user322\",\"givenName\":\"User 322\",\"familyName\":\"User 322\",\"userRoles\":[{\"role\":{\"name\":\"ROLE_USER\"}}],\"lastLoginDateTime\":\""
				+ optUser.get().getLastLoginDateTime() + "\",\"previousLoginDateTime\":\""
				+ optUser.get().getLastLoginDateTime() + "\"}";

		this.mvc.perform(get(requestUrl).header("Authorization", AuthenticationService.createBearerToken("user322"))
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().is(expectedStatus))
				.andExpect(content().json(expectedJson));

	}

	@Test
	void testGoogleLoginVerificationErrorUnauthorized401() throws Exception {
		String requestUrl = "/google-login";
		String requestJson = "{\"idToken\":\"abcdef\"}";
		int expectedStatus = 401;
		String expectedJson = "";

		given(googleIdTokenVerifier.verify("abcdef")).willThrow(new GeneralSecurityException());

		this.mvc.perform(post(requestUrl).content(requestJson).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is(expectedStatus)).andExpect(content().string(expectedJson))
				.andExpect(header().doesNotExist("Authorization"));
	}

	@Test
	void testGoogleLoginInvalidJsonUnauthorized401() throws Exception {
		String requestUrl = "/google-login";
		String requestJson = "{\"idTokenx\":\"abcdef\"}";
		int expectedStatus = 401;
		String expectedJson = "";

		this.mvc.perform(post(requestUrl).content(requestJson).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is(expectedStatus)).andExpect(content().string(expectedJson))
				.andExpect(header().doesNotExist("Authorization"));
	}

	@Test
	void testGoogleLoginInvalidTokenUnauthorized401() throws Exception {
		String requestUrl = "/google-login";
		String requestJson = "{\"idToken\":\"abcdef\"}";
		int expectedStatus = 401;
		String expectedJson = "";

		given(googleIdTokenVerifier.verify("abcdef")).willReturn(null);

		this.mvc.perform(post(requestUrl).content(requestJson).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is(expectedStatus)).andExpect(content().string(expectedJson))
				.andExpect(header().doesNotExist("Authorization"));
	}

}