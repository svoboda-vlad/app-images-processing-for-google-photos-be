package svobodavlad.imagesprocessing.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import svobodavlad.imagesprocessing.testutil.SecurityTestUtil;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("h2")
//@WithMockUser - not needed
class UserControllerIT {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private SecurityTestUtil securityTestUtil;
	
	@BeforeEach
	void initData() {
		securityTestUtil.saveAdminUser();
		securityTestUtil.saveDefaultUser();
	}

	@Test
	void testGetCurrentUserOk200() throws Exception {
		String requestUrl = "/user";
		int expectedStatus = 200;
		String expectedJson = "{\"username\":\"user1\",\"givenName\":\"User 1\",\"familyName\":\"User 1\",\"userRoles\":[{\"role\":{\"name\":\"ROLE_USER\"}}],\"lastLoginDateTime\":null,\"previousLoginDateTime\":null}";

		this.mvc.perform(get(requestUrl).header("Authorization", SecurityTestUtil.createBearerTokenDefaultUser())
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().is(expectedStatus))
				.andExpect(content().json(expectedJson));
	}

	@Test
	void testGetCurrentUserMissingAuthroizationHeaderNotFound404() throws Exception {
		String requestUrl = "/user";
		int expectedStatus = 404;
		String expectedJson = "";

		this.mvc.perform(get(requestUrl).accept(MediaType.APPLICATION_JSON)).andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
	}

	@Test
	void testGetCurrentUserInvalidAuthroizationHeaderNotFound404() throws Exception {
		String requestUrl = "/user";
		int expectedStatus = 404;
		String expectedJson = "";

		this.mvc.perform(get(requestUrl).header("Authorization", SecurityTestUtil.createBearerTokenForUsername("invaliduser"))
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
	}

	@Test
	void testGetCurrentUserInvalidTokenNotFound404() throws Exception {
		String requestUrl = "/user";
		int expectedStatus = 404;
		String expectedJson = "";

		this.mvc.perform(get(requestUrl).header("Authorization", SecurityTestUtil.createBearerTokenDefaultUser() + "xxx")
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));

	}

	@Test
	void testRegisterUserCreated201() throws Exception {
		String requestUrl = "/user";
		String requestJson = "{\"username\": \"usernew\", \"password\": \"pass123new\",\"givenName\": \"Test 1\",\"familyName\": \"Test 1\"}";
		int expectedStatus = 201;
		String expectedJson = "";

		this.mvc.perform(post(requestUrl).content(requestJson).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is(expectedStatus)).andExpect(content().string(expectedJson));

		requestUrl = "/user";
		expectedStatus = 200;
		expectedJson = "{\"username\":\"usernew\",\"givenName\":\"Test 1\",\"familyName\":\"Test 1\",\"userRoles\":[{\"role\":{\"name\":\"ROLE_USER\"}}],\"lastLoginDateTime\":null,\"previousLoginDateTime\":null}";

		this.mvc.perform(get(requestUrl).header("Authorization", SecurityTestUtil.createBearerTokenForUsername("usernew"))
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().is(expectedStatus))
				.andExpect(content().json(expectedJson));

	}

	@Test
	void testRegisterUserExistsAlreadyBadRequest400() throws Exception {
		String requestUrl = "/user";
		String requestJson = "{\"username\": \"user1\", \"password\": \"pass123\",\"givenName\": \"User 1\",\"familyName\": \"User 1\"}";
		int expectedStatus = 400;
		String expectedJson = "";

		this.mvc.perform(post(requestUrl).content(requestJson).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is(expectedStatus)).andExpect(content().string(expectedJson));
	}

	@Test
	void testUpdateUserOk200() throws Exception {
		String requestUrl = "/user";
		String requestJson = "{\"username\":\"user1\",\"lastLoginDateTime\":null,\"previousLoginDateTime\":null, \"givenName\": \"User X\",\"familyName\": \"User Y\"}";
		int expectedStatus = 200;
		String expectedJson = "{\"username\":\"user1\",\"givenName\":\"User X\",\"familyName\":\"User Y\",\"userRoles\":[{\"role\":{\"name\":\"ROLE_USER\"}}],\"lastLoginDateTime\":null,\"previousLoginDateTime\":null}";

		this.mvc.perform(put(requestUrl).header("Authorization", SecurityTestUtil.createBearerTokenDefaultUser())
				.content(requestJson).contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(expectedStatus))
				.andExpect(content().json(expectedJson));
	}

	@Test
	void testUpdateUserUsernameDoesNotMatchBadRequest400() throws Exception {
		String requestUrl = "/user";
		String requestJson = "{\"username\":\"userx\",\"lastLoginDateTime\":null,\"previousLoginDateTime\":null, \"givenName\": \"User X\",\"familyName\": \"User Y\"}";
		int expectedStatus = 400;
		String expectedJson = "";

		this.mvc.perform(put(requestUrl).header("Authorization", SecurityTestUtil.createBearerTokenDefaultUser())
				.content(requestJson).contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(expectedStatus))
				.andExpect(content().string(expectedJson));
	}

	@Test
	void testDeleteUserNoContent204() throws Exception {
		String requestUrl = "/user";
		int expectedStatus = 204;
		String expectedJson = "";

		this.mvc.perform(delete(requestUrl).header("Authorization", SecurityTestUtil.createBearerTokenDefaultUser()))
				.andExpect(status().is(expectedStatus)).andExpect(content().string(expectedJson));
	}

}