package svobodavlad.imagesprocessing.testutil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import svobodavlad.imagesprocessing.jpaentities.User;
import svobodavlad.imagesprocessing.jpaentities.User.LoginProvider;
import svobodavlad.imagesprocessing.security.AuthenticationService;
import svobodavlad.imagesprocessing.security.UserService;

@Component
public class SecurityTestUtil {

	@Autowired
	private PasswordEncoder encoder;

	@Autowired
	private UserService userService;

	private static final String ADMIN_USERNAME = "admin";
	private static final String ADMIN_PASSWORD = "admin123";
	private static final String ADMIN_GIVEN_NAME = "Administrator";
	private static final String ADMIN_FAMILY_NAME = "Administrator";
	private static final String DEFAULT_USERNAME = "user1";
	private static final String DEFAULT_PASSWORD = "pass123";
	private static final String DEFAULT_GIVEN_NAME = "User 1";
	private static final String DEFAULT_FAMILY_NAME = "User 1";
	private static final String DEFAULT_USERNAME_GOOGLE = "usergoogle1";

	public User saveAdminUser() {
		User user = new User(ADMIN_USERNAME, encoder.encode(ADMIN_PASSWORD), LoginProvider.INTERNAL, ADMIN_GIVEN_NAME,
				ADMIN_FAMILY_NAME);
		return userService.registerAdminUser(user);
	}
	
	public User saveDefaultUserInternal() {
		User user = new User(DEFAULT_USERNAME, encoder.encode(DEFAULT_PASSWORD), LoginProvider.INTERNAL, DEFAULT_GIVEN_NAME,
				DEFAULT_FAMILY_NAME);
		return userService.registerUser(user);
	}

	public User saveDefaultUserGoogle() {
		User user = new User(DEFAULT_USERNAME_GOOGLE, encoder.encode(DEFAULT_USERNAME_GOOGLE), LoginProvider.GOOGLE, DEFAULT_GIVEN_NAME,
				DEFAULT_FAMILY_NAME);
		return userService.registerUser(user);
	}	
	
	public static String createBearerTokenAdminUser() {
		return AuthenticationService.createBearerToken(ADMIN_USERNAME);
	}
	
	public static String createBearerTokenDefaultUser() {
		return AuthenticationService.createBearerToken(DEFAULT_USERNAME);
	}

	public static String createBearerTokenForUsername(String username) {
		return AuthenticationService.createBearerToken(username);
	}	
	
}