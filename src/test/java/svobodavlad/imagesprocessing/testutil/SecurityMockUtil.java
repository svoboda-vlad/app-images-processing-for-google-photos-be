package svobodavlad.imagesprocessing.testutil;

import svobodavlad.imagesprocessing.security.Role;
import svobodavlad.imagesprocessing.security.User;
import svobodavlad.imagesprocessing.security.User.LoginProvider;

public class SecurityMockUtil {
	
	private static final String ADMIN_USERNAME = "admin";
	private static final String ADMIN_PASSWORD = "A".repeat(60);
	private static final String ADMIN_GIVEN_NAME = "Administrator";
	private static final String ADMIN_FAMILY_NAME = "Administrator";
	private static final String ROLE_USER = "ROLE_USER";
	private static final String ROLE_ADMIN = "ROLE_ADMIN";
	private static final String DEFAULT_USERNAME = "user1";
	private static final String DEFAULT_PASSWORD = "A".repeat(60);
	private static final String DEFAULT_GIVEN_NAME = "User 1";
	private static final String DEFAULT_FAMILY_NAME = "User 1";
	private static final String DEFAULT_USERNAME_GOOGLE = "usergoogle1";	
		
	public static User getMockedAdminUser() {
		User user = new User(ADMIN_USERNAME, ADMIN_PASSWORD, LoginProvider.INTERNAL, ADMIN_GIVEN_NAME, ADMIN_FAMILY_NAME, null, null);
		user.addRole(new Role(ROLE_USER));
		user.addRole(new Role(ROLE_ADMIN));
		return user;
	}
	
	public static User getMockedDefaultUserInternal() {
		User user = new User(DEFAULT_USERNAME, DEFAULT_PASSWORD, LoginProvider.INTERNAL, DEFAULT_GIVEN_NAME, DEFAULT_FAMILY_NAME, null, null);
		user.addRole(new Role(ROLE_USER));
		return user;
	}
	
	public static User getMockedDefaultUserGoogle() {
		User user = new User(DEFAULT_USERNAME_GOOGLE, DEFAULT_USERNAME_GOOGLE, LoginProvider.GOOGLE, DEFAULT_GIVEN_NAME, DEFAULT_FAMILY_NAME, null, null);
		user.addRole(new Role(ROLE_USER));
		return user;
	}	

}
