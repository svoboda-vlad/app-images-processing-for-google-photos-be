package svobodavlad.imagesprocessing.google;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;

import svobodavlad.imagesprocessing.jpaentities.User;
import svobodavlad.imagesprocessing.jpaentities.User.LoginProvider;
import svobodavlad.imagesprocessing.security.AuthenticationService;
import svobodavlad.imagesprocessing.security.UserRegister;
import svobodavlad.imagesprocessing.security.UserRepository;
import svobodavlad.imagesprocessing.security.UserService;

public class GoogleLoginFilter extends UsernamePasswordAuthenticationFilter {

	private final Logger log = LoggerFactory.getLogger(GoogleLoginFilter.class);
	
	private static final AntPathRequestMatcher GOOGLE_ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher("/google-login",
			"POST");

	@Autowired
	private PasswordEncoder encoder;

	@Autowired
	private GoogleIdTokenVerifier googleIdTokenVerifier;

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;
	
	public GoogleLoginFilter(AuthenticationManager authManager) {
		super(authManager);
		this.setRequiresAuthenticationRequestMatcher(GOOGLE_ANT_PATH_REQUEST_MATCHER);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
			throws AuthenticationException {

		GoogleIdTokenTemplate tokenEntity = resolveGoogleIdTokenTemplate(req);
		if (tokenEntity == null)
			throw new BadCredentialsException("");
		String username = "";

		try {
			GoogleIdToken idToken = googleIdTokenVerifier.verify(tokenEntity.getIdToken());
			if (idToken != null) {
				Payload payload = idToken.getPayload();
				username = payload.getSubject();

				Optional<User> optUser = userRepository.findByUsername(username);
				if (optUser.isPresent() && optUser.get().getLoginProvider() != LoginProvider.GOOGLE)
					throw new BadCredentialsException("");
				if (optUser.isEmpty()) {
					String familyName = (String) payload.get("family_name");
					String givenName = (String) payload.get("given_name");
					UserRegister userRegister = new UserRegister(username, username, givenName, familyName);
					User user = userRegister.toUserGoogle(encoder);
					userService.registerUser(user);
				}
			}
		} catch (GeneralSecurityException | IOException e) {
			log.info("Google ID token verification failed");
			throw new BadCredentialsException("");
		}
		return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(username, username));
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain,
			Authentication auth) throws IOException, ServletException {
		AuthenticationService.addToken(res, auth.getName());
		userService.updateLastLoginDateTime(auth.getName());
	}

	private GoogleIdTokenTemplate resolveGoogleIdTokenTemplate(HttpServletRequest request) {
		try {
			return new ObjectMapper().readValue(request.getInputStream(), GoogleIdTokenTemplate.class);
		} catch (Exception e) {
			log.info("ID token parsing from request body failed: {}.", e.getMessage());
		}
		return null;
	}

}