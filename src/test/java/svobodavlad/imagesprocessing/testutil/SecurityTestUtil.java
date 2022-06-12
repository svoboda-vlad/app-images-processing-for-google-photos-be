package svobodavlad.imagesprocessing.testutil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import svobodavlad.imagesprocessing.jpaentities.User;
import svobodavlad.imagesprocessing.security.UserRegister;
import svobodavlad.imagesprocessing.security.UserService;

@Component
public class SecurityTestUtil {

	@Autowired
	private UserService userService;

	public static final String ADMIN_USERNAME = "admin";
	public static final String ADMIN_GIVEN_NAME = "admin";
	public static final String ADMIN_FAMILY_NAME = "admin";
	public static final String ADMIN_EMAIL = null;
	
	public static final String DEFAULT_USERNAME = "user";
	public static final String DEFAULT_GIVEN_NAME = "user";
	public static final String DEFAULT_FAMILY_NAME = "user";
	public static final String DEFAULT_EMAIL = null;

	public User saveAdminUser() {
		return userService.registerAdminUser(new UserRegister(
				ADMIN_USERNAME, 
				ADMIN_GIVEN_NAME, 
				ADMIN_FAMILY_NAME, 
				ADMIN_EMAIL)
				.toUser()
				);
	}

	public User saveDefaultUser() {
		return userService.registerUser(new UserRegister(
				DEFAULT_USERNAME, 
				DEFAULT_GIVEN_NAME, 
				DEFAULT_FAMILY_NAME, 
				DEFAULT_EMAIL)
				.toUser()
				);
	}
}