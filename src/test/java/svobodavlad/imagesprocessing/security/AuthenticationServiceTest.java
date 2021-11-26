package svobodavlad.imagesprocessing.security;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import io.jsonwebtoken.Jwts;
import svobodavlad.imagesprocessing.jpaentities.User;
import svobodavlad.imagesprocessing.testutil.SecurityMockUtil;
import svobodavlad.imagesprocessing.testutil.UnitTestTemplate;

class AuthenticationServiceTest extends UnitTestTemplate {

	@MockBean
	private UserDetailsService userDetailsService;

	@Autowired
	private AuthenticationService authenticationService;

	@Test
	@Disabled
	void testAddToken() {
		String username = "user1";
		Date expirationDateTime = Date.from(LocalDateTime.now().plusMinutes(AuthenticationService.EXPIRY_MINS)
				.atZone(ZoneId.systemDefault()).toInstant());
		String jwtToken = Jwts.builder().setSubject(username).setExpiration(expirationDateTime)
				.signWith(AuthenticationService.SIGNINGKEY).compact();
		String expectedHeaderValue = AuthenticationService.PREFIX + " " + jwtToken;

		HttpServletResponse mockResponse = Mockito.mock(HttpServletResponse.class);
		AuthenticationService.addToken(mockResponse, username);

		this.verify(mockResponse, this.times(1)).addHeader(AuthenticationService.AUTHORIZATION, expectedHeaderValue);
	}

	@Test
	void testGetAuthenticationOk() {
		User mockedUser = SecurityMockUtil.getMockedDefaultUserInternal();
		Date expirationDateTime = Date.from(LocalDateTime.now().plusMinutes(AuthenticationService.EXPIRY_MINS)
				.atZone(ZoneId.systemDefault()).toInstant());
		String jwtToken = Jwts.builder().setSubject(mockedUser.getUsername()).setExpiration(expirationDateTime)
				.signWith(AuthenticationService.SIGNINGKEY).compact();
		String headerValue = AuthenticationService.PREFIX + " " + jwtToken;
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader(AuthenticationService.AUTHORIZATION, headerValue);

		this.given(userDetailsService.loadUserByUsername(mockedUser.getUsername())).willReturn(mockedUser);

		this.assertThat(authenticationService.getAuthentication(request)).isEqualTo(
				new UsernamePasswordAuthenticationToken(mockedUser.getUsername(), null, mockedUser.getAuthorities()));
	}

	@Test
	void testGetAuthenticationMissingUsername() {
		Date expirationDateTime = Date.from(LocalDateTime.now().plusMinutes(AuthenticationService.EXPIRY_MINS)
				.atZone(ZoneId.systemDefault()).toInstant());
		String jwtToken = Jwts.builder().setSubject(null).setExpiration(expirationDateTime)
				.signWith(AuthenticationService.SIGNINGKEY).compact();
		String headerValue = AuthenticationService.PREFIX + " " + jwtToken;
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader(AuthenticationService.AUTHORIZATION, headerValue);

		this.assertThat(authenticationService.getAuthentication(request)).isNull();
	}
	
	@Test
	void testGetAuthenticationMissingAuthorizationHeader() {
		MockHttpServletRequest request = new MockHttpServletRequest();

		this.assertThat(authenticationService.getAuthentication(request)).isNull();
	}
	
	@Test
	void testGetAuthenticationInvalidToken() {
		String jwtToken = "invalidtoken";
		String headerValue = AuthenticationService.PREFIX + " " + jwtToken;
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader(AuthenticationService.AUTHORIZATION, headerValue);

		this.assertThat(authenticationService.getAuthentication(request)).isNull();
	}	

	@Test
	void testGetAuthenticationExpiredToken() {
		User mockedUser = SecurityMockUtil.getMockedDefaultUserInternal();
		Date expirationDateTime = Date
				.from(LocalDateTime.now().minusDays(1).atZone(ZoneId.systemDefault()).toInstant());
		String jwtToken = Jwts.builder().setSubject(mockedUser.getUsername()).setExpiration(expirationDateTime)
				.signWith(AuthenticationService.SIGNINGKEY).compact();
		String headerValue = AuthenticationService.PREFIX + " " + jwtToken;
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader(AuthenticationService.AUTHORIZATION, headerValue);

		this.assertThat(authenticationService.getAuthentication(request)).isNull();
	}
	
	@Test
	void testGetAuthenticationUsernameNotFound() {
		User mockedUser = SecurityMockUtil.getMockedDefaultUserInternal();
		Date expirationDateTime = Date.from(LocalDateTime.now().plusMinutes(AuthenticationService.EXPIRY_MINS)
				.atZone(ZoneId.systemDefault()).toInstant());
		String jwtToken = Jwts.builder().setSubject(mockedUser.getUsername()).setExpiration(expirationDateTime)
				.signWith(AuthenticationService.SIGNINGKEY).compact();
		String headerValue = AuthenticationService.PREFIX + " " + jwtToken;
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader(AuthenticationService.AUTHORIZATION, headerValue);

		this.given(userDetailsService.loadUserByUsername(mockedUser.getUsername())).willThrow(UsernameNotFoundException.class);

		this.assertThat(authenticationService.getAuthentication(request)).isNull();
	}	

	@Test
	void testCreateBearerToken() {
		this.assertThat(AuthenticationService.createBearerToken("user1").length()).isEqualTo(158);
	}

}
