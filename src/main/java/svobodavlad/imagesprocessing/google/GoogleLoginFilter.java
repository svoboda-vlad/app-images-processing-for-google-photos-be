package svobodavlad.imagesprocessing.google;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;

import svobodavlad.imagesprocessing.jpaentities.User;
import svobodavlad.imagesprocessing.jpaentities.User.LoginProvider;
import svobodavlad.imagesprocessing.jpaentities.UserRoles;
import svobodavlad.imagesprocessing.security.AuthenticationService;
import svobodavlad.imagesprocessing.security.UserInfo;
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
	
	private Optional<GoogleIdToken> optIdToken;
	
	public GoogleLoginFilter(AuthenticationManager authManager) {
		super(authManager);
		this.setRequiresAuthenticationRequestMatcher(GOOGLE_ANT_PATH_REQUEST_MATCHER);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
			throws AuthenticationException {

		Optional<GoogleIdTokenTemplate> optTokenEntity = resolveGoogleIdTokenTemplate(req);
		if (optTokenEntity.isEmpty()) throw new BadCredentialsException("");
		String username = "";

		try {
			GoogleIdToken idToken = googleIdTokenVerifier.verify(optTokenEntity.get().getIdToken());
			if (idToken != null) {
				this.optIdToken = Optional.of(idToken);
				username = idToken.getPayload().getSubject();
				Optional<User> optUser = userRepository.findByUsername(username);
				
				if (optUser.isPresent()) {
					if (optUser.get().getLoginProvider() != LoginProvider.GOOGLE) 
						throw new BadCredentialsException("");
				} else {
					extractPayloadFromIdToken().ifPresent(userInfo -> {
						UserRegister userRegister = new UserRegister(userInfo.getUsername(), userInfo.getUsername(), userInfo.getGivenName(), userInfo.getFamilyName(), userInfo.getEmail());
						User user = userRegister.toUserGoogle(encoder);
						userService.registerUser(user);						
					});
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
		SecurityContextHolder.getContext().setAuthentication(auth);
		AuthenticationService.addToken(res, auth.getName());
		extractPayloadFromIdToken().ifPresent(userInfo -> userService.updateCurrentUser(userInfo));
		userService.updateCurrentUserLastLoginDateTime();
	}

	private Optional<GoogleIdTokenTemplate> resolveGoogleIdTokenTemplate(HttpServletRequest request) {
		try {
			return Optional.of(new ObjectMapper().readValue(request.getInputStream(), GoogleIdTokenTemplate.class));
		} catch (Exception e) {
			log.info("ID token parsing from request body failed: {}.", e.getMessage());
			return Optional.empty();
		}
	}
	
	private Optional<UserInfo> extractPayloadFromIdToken() {
		if (this.optIdToken.isEmpty()) return Optional.empty();
		Payload payload = this.optIdToken.get().getPayload();
		return Optional.of(new UserInfo(payload.getSubject(),
			(String) payload.get("given_name"), 
			(String) payload.get("family_name"),
			(String) payload.getEmail(),			
			null, null, new ArrayList<UserRoles>()));
	}	

}