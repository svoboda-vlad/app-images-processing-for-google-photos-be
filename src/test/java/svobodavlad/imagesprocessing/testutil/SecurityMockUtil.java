package svobodavlad.imagesprocessing.testutil;

import svobodavlad.imagesprocessing.jpaentities.Role;
import svobodavlad.imagesprocessing.jpaentities.User;
import svobodavlad.imagesprocessing.jpaentities.User.LoginProvider;

public class SecurityMockUtil {
	
	private static final String ADMIN_USERNAME = "admin";
	private static final String ADMIN_PASSWORD = "A".repeat(60);
	private static final String ADMIN_GIVEN_NAME = "Administrator";
	private static final String ADMIN_FAMILY_NAME = "Administrator";
	private static final String ADMIN_EMAIL = "admin@gmail.com";	
	private static final String ROLE_USER = "ROLE_USER";
	private static final String ROLE_ADMIN = "ROLE_ADMIN";
	public static final String DEFAULT_USERNAME = "user1";
	private static final String DEFAULT_PASSWORD = "A".repeat(60);
	private static final String DEFAULT_GIVEN_NAME = "User 1";
	private static final String DEFAULT_FAMILY_NAME = "User 1";
	private static final String DEFAULT_EMAIL = "user1@gmail.com";	
	private static final String DEFAULT_USERNAME_GOOGLE = "usergoogle1";	
		
	public static User getMockedAdminUser() {
		User user = new User(ADMIN_USERNAME, ADMIN_PASSWORD, LoginProvider.INTERNAL, ADMIN_GIVEN_NAME, ADMIN_FAMILY_NAME);
		user.setEmail(ADMIN_EMAIL);
		user.addRole(new Role(ROLE_USER));
		Role adminRole = new Role(ROLE_ADMIN);
		adminRole.setId(1L);
		user.addRole(adminRole);
		return user;
	}
	
	public static User getMockedDefaultUserInternal() {
		User user = new User(DEFAULT_USERNAME, DEFAULT_PASSWORD, LoginProvider.INTERNAL, DEFAULT_GIVEN_NAME, DEFAULT_FAMILY_NAME);
		user.setEmail(DEFAULT_EMAIL);
		user.addRole(new Role(ROLE_USER));
		return user;
	}
	
	public static User getMockedDefaultUserGoogle() {
		User user = new User(DEFAULT_USERNAME_GOOGLE, DEFAULT_USERNAME_GOOGLE, LoginProvider.GOOGLE, DEFAULT_GIVEN_NAME, DEFAULT_FAMILY_NAME);
		user.setEmail(DEFAULT_EMAIL);
		user.addRole(new Role(ROLE_USER));
		return user;
	}	

}
