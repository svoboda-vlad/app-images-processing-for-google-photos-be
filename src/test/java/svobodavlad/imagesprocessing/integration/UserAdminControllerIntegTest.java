package svobodavlad.imagesprocessing.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import svobodavlad.imagesprocessing.testutil.SecurityTestUtil;

@SpringBootTest
@AutoConfigureMockMvc
//@WithMockUser - not needed
class UserAdminControllerIntegTest {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private SecurityTestUtil securityTestUtil;
	
	@BeforeEach
	void initData() {
		securityTestUtil.saveAdminUser();
		securityTestUtil.saveDefaultUser();
	}

	@AfterEach
	void cleanData() {
		securityTestUtil.deleteAllUsers();
	}

	@Test
	void testGetAllUsersOk200() throws Exception {
		String requestUrl = "/admin/users";
		int expectedStatus = 200;
		String expectedJson = "[{\"username\":\"user1\",\"givenName\":\"User 1\",\"familyName\":\"User 1\",\"lastLoginDateTime\":null,\"previousLoginDateTime\":null,\"userRoles\":[{\"role\":{\"name\":\"ROLE_USER\"}}]},"
				+ "{\"username\":\"admin\",\"givenName\":\"Administrator\",\"familyName\":\"Administrator\",\"lastLoginDateTime\":null,\"previousLoginDateTime\":null,\"userRoles\":[{\"role\":{\"name\":\"ROLE_USER\"}},{\"role\":{\"name\":\"ROLE_ADMIN\"}}]}]";

		this.mvc.perform(get(requestUrl).header("Authorization", SecurityTestUtil.createBearerTokenAdminUser())
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().is(expectedStatus))
				.andExpect(content().json(expectedJson));
	}

}
