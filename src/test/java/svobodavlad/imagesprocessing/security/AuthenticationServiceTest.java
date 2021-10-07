package svobodavlad.imagesprocessing.security;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import io.jsonwebtoken.Jwts;
import svobodavlad.imagesprocessing.testutil.UnitTestTemplate;

class AuthenticationServiceTest extends UnitTestTemplate {

	@Test
	void testAddToken() {
		String username = "user1";
		String expectedHeaderName = "Authorization";
		
		Date expirationDateTime = Date.from(LocalDateTime.now().plusMinutes(AuthenticationService.EXPIRY_MINS)
				.atZone(ZoneId.systemDefault()).toInstant());
		String jwtToken = Jwts.builder().setSubject(username).setExpiration(expirationDateTime)
				.signWith(AuthenticationService.SIGNINGKEY).compact();
		String expectedHeaderValue = AuthenticationService.PREFIX + " " + jwtToken;
		HttpServletResponse mockResponse = Mockito.mock(HttpServletResponse.class);

		AuthenticationService.addToken(mockResponse, username);

		this.verify(mockResponse, this.times(1)).addHeader(expectedHeaderName, expectedHeaderValue);
	}

	/*
	 * @Test void testGetAuthentication() { fail("Not yet implemented"); }
	 */

	@Test
	void testCreateBearerToken() {
		this.assertThat(AuthenticationService.createBearerToken("user1").length()).isEqualTo(158);
	}

}
