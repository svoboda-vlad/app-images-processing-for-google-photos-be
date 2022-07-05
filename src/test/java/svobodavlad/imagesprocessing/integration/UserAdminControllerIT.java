package svobodavlad.imagesprocessing.integration;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;

import svobodavlad.imagesprocessing.security.UserTemplate;
import svobodavlad.imagesprocessing.testutil.IntegTestTemplate;
import svobodavlad.imagesprocessing.testutil.SecurityTestUtil;

public class UserAdminControllerIT extends IntegTestTemplate {
	
	private static final String USERNAME = SecurityTestUtil.DEFAULT_USERNAME;
	private static final String EMAIL = null;
	
	private static final String ADMIN_USERS_URL = "/admin/users";
	
	@Autowired
	private SecurityTestUtil securityTestUtil;
    @Autowired
    private JacksonTester<List<UserTemplate>> jacksonTester;
	
	@BeforeEach
	void initData() {
		securityTestUtil.saveDefaultUser();
	}	

	@Test
	void getAllUsersOk200() throws Exception {
		var userTemplate = new UserTemplate().setUsername(USERNAME).setGivenName(USERNAME).setFamilyName(USERNAME).setEmail(EMAIL);
		var userTemplateList = List.of(userTemplate);
		var expectedJson = jacksonTester.write(userTemplateList).getJson();		
		
		var mvcResult = mockMvcPerformGetAuthorizationAdminUser(ADMIN_USERS_URL);
		mockMvcExpectStatusAndContent(mvcResult, HTTP_OK, expectedJson);
	}

}
