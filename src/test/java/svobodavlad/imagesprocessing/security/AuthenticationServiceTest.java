package svobodavlad.imagesprocessing.security;

import org.junit.jupiter.api.Test;

import svobodavlad.imagesprocessing.testutil.UnitTestTemplate;

class AuthenticationServiceTest extends UnitTestTemplate {

	/*@Test
	void testAddToken() {
		fail("Not yet implemented");
	}

	@Test
	void testGetAuthentication() {
		fail("Not yet implemented");
	}*/

	@Test
	void testCreateBearerToken() {
		this.assertThat(AuthenticationService.createBearerToken("user1").length()).isEqualTo(158);
	}

}
