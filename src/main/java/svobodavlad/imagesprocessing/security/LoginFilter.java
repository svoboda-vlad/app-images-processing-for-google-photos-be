package svobodavlad.imagesprocessing.security;

import java.io.IOException;
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
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;

import svobodavlad.imagesprocessing.jpaentities.User;
import svobodavlad.imagesprocessing.jpaentities.User.LoginProvider;

public class LoginFilter extends AbstractAuthenticationProcessingFilter {

	private final Logger log = LoggerFactory.getLogger(LoginFilter.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserService userService;

	public LoginFilter(AuthenticationManager authManager) {
		super(new AntPathRequestMatcher("/login", "POST"));
		this.setAuthenticationManager(authManager);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
			throws AuthenticationException, IOException {
		LoginUser loginUser = resolveUser(req);

		if (loginUser == null)
			throw new BadCredentialsException("");

		Optional<User> optUser = userRepository.findByUsername(loginUser.getUsername());
		if (optUser.isPresent() && optUser.get().getLoginProvider() != LoginProvider.INTERNAL) throw new BadCredentialsException("");
		
		return getAuthenticationManager().authenticate(
				new UsernamePasswordAuthenticationToken(loginUser.getUsername(), loginUser.getPassword()));
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain,
			Authentication auth) throws IOException, ServletException {
		AuthenticationService.addToken(res, auth.getName());
		userService.updateLastLoginDateTime(auth.getName());
	}

	private LoginUser resolveUser(HttpServletRequest request) {
		try {
			return new ObjectMapper().readValue(request.getInputStream(), LoginUser.class);
		} catch (Exception e) {
			log.info("Username and password parsing from request body failed: {}.", e.getMessage());
		}
		return null;
	}
}