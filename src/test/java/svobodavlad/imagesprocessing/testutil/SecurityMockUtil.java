package svobodavlad.imagesprocessing.testutil;

import svobodavlad.imagesprocessing.security.Role;
import svobodavlad.imagesprocessing.security.User;
import svobodavlad.imagesprocessing.security.User.LoginProvider;

public class SecurityMockUtil {
	
	private static final String ADMIN_USERNAME = "admin";
	private static final String ADMIN_PASSWORD = "A".repeat(60);
	private static final String ADMIN_GIVEN_NAME = "Administrator";
	private static final String ADMIN_FAMILY_NAME = "Administrator";
	private static final String USER_USERNAME = "user1";
	private static final String USER_PASSWORD = "A".repeat(60);
	private static final String USER_GIVEN_NAME = "User 1";
	private static final String USER_FAMILY_NAME = "User 1";	
	private static final String ROLE_USER = "ROLE_USER";
	private static final String ROLE_ADMIN = "ROLE_ADMIN";	
		
	public static User getMockedAdminUser() {
		User user = new User(ADMIN_USERNAME, ADMIN_PASSWORD, LoginProvider.INTERNAL, ADMIN_GIVEN_NAME, ADMIN_FAMILY_NAME, null, null);
		user.addRole(new Role(ROLE_USER));
		user.addRole(new Role(ROLE_ADMIN));
		return user;
	}
	
	public static User getMockedDefaultUser() {
		User user = new User(USER_USERNAME, USER_PASSWORD, LoginProvider.INTERNAL, USER_GIVEN_NAME, USER_FAMILY_NAME, null, null);
		user.addRole(new Role(ROLE_USER));
		return user;
	}	

}
