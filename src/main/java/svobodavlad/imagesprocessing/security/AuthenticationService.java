package svobodavlad.imagesprocessing.security;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

	private final Logger log = LoggerFactory.getLogger(AuthenticationService.class);

	static final long EXPIRY_MINS = 60L;
	// We need a signing key, so we'll create one just for this example. Usually
	// the key would be read from your application configuration instead.
	static final Key SIGNINGKEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);
	static final String PREFIX = "Bearer";
	static final String AUTHORIZATION = "Authorization";
	private final UserDetailsService userDetailsService;

	static public void addToken(HttpServletResponse res, String username) {
		String jwtToken = createBearerToken(username);
		res.addHeader(AUTHORIZATION, jwtToken);
		res.addHeader("Access-Control-Expose-Headers", "Authorization");
	}

	public Authentication getAuthentication(HttpServletRequest request) {
		Optional<String> optToken = resolveToken(request);
		if (optToken.isEmpty()) {
			log.info("JWT token not found.");
			return null;
		}
		Optional<String> optUsername = getUsername(validateToken(optToken.get()));
		if (optUsername.isEmpty()) return null;

		try {
			UserDetails userDetails = userDetailsService.loadUserByUsername(optUsername.get());
			return new UsernamePasswordAuthenticationToken(optUsername.get(), null, userDetails.getAuthorities());
		} catch (UsernameNotFoundException e) {
			return null;
		}
	}

	private Optional<String> resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader(AUTHORIZATION);
		if (bearerToken == null) return Optional.empty();
		if (!bearerToken.startsWith(PREFIX)) return Optional.empty();
		return Optional.of(bearerToken.replace(PREFIX, "").trim());
	}

	private Optional<Jws<Claims>> validateToken(String token) {
		try {
			Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(SIGNINGKEY).build().parseClaimsJws(token);
			return Optional.of(claims);
		} catch (RuntimeException e) {
			log.info("JWT token validation failed: {}.", e.getMessage());
		}
		return Optional.empty();
	}

	private Optional<String> getUsername(Optional<Jws<Claims>> optClaims) {
		if (optClaims.isEmpty()) return Optional.empty();
		return Optional.of(optClaims.get().getBody().getSubject());
	}

	private static String generateToken(String username) {
		Date expirationDateTime = Date
				.from(LocalDateTime.now().plusMinutes(EXPIRY_MINS).atZone(ZoneId.systemDefault()).toInstant());
		return Jwts.builder().setSubject(username).setExpiration(expirationDateTime).signWith(SIGNINGKEY).compact();
	}
	
	public static String createBearerToken(String username) {
		return PREFIX + " " + generateToken(username);
	}

}