package svobodavlad.imagesprocessing.testutil;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import svobodavlad.imagesprocessing.security.AuthenticationService;
import svobodavlad.imagesprocessing.security.Role;
import svobodavlad.imagesprocessing.security.RoleRepository;
import svobodavlad.imagesprocessing.security.User;
import svobodavlad.imagesprocessing.security.User.LoginProvider;
import svobodavlad.imagesprocessing.security.UserRepository;

@Component
public class SecurityTestUtil {

	@Autowired
	private PasswordEncoder encoder;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	private static final String ADMIN_USERNAME = "admin";
	private static final String ADMIN_PASSWORD = "admin123";
	private static final String ADMIN_GIVEN_NAME = "Administrator";
	private static final String ADMIN_FAMILY_NAME = "Administrator";
	private static final String ROLE_USER = "ROLE_USER";
	private static final String ROLE_ADMIN = "ROLE_ADMIN";
	private static final String DEFAULT_USERNAME = "user1";
	private static final String DEFAULT_PASSWORD = "pass123";
	private static final String DEFAULT_GIVEN_NAME = "User 1";
	private static final String DEFAULT_FAMILY_NAME = "User 1";

	public void saveAdminUser() {
		User user = new User(ADMIN_USERNAME, encoder.encode(ADMIN_PASSWORD), LoginProvider.INTERNAL, ADMIN_GIVEN_NAME,
				ADMIN_FAMILY_NAME);
		user = userRepository.save(user);
		Optional<Role> optRole1 = roleRepository.findByName(ROLE_USER);
		Optional<Role> optRole2 = roleRepository.findByName(ROLE_ADMIN);
		user.addRole(optRole1.get());
		user.addRole(optRole2.get());
		userRepository.save(user);
	}
	
	public void saveDefaultUser() {
		User user = new User(DEFAULT_USERNAME, encoder.encode(DEFAULT_PASSWORD), LoginProvider.INTERNAL, DEFAULT_GIVEN_NAME,
				DEFAULT_FAMILY_NAME);
		user = userRepository.save(user);
		Optional<Role> optRole1 = roleRepository.findByName(ROLE_USER);
		user.addRole(optRole1.get());
		userRepository.save(user);
	}

	public void deleteAllUsers() {
		userRepository.deleteAll();
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