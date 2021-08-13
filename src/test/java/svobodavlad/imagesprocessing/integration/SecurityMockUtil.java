package svobodavlad.imagesprocessing.integration;

import svobodavlad.imagesprocessing.security.Role;
import svobodavlad.imagesprocessing.security.User;
import svobodavlad.imagesprocessing.security.User.LoginProvider;

public class SecurityMockUtil {
	
	private static final String ADMIN_USERNAME = "admin";
	private static final String ADMIN_GIVEN_NAME = "Administrator";
	private static final String ADMIN_FAMILY_NAME = "Administrator";
	private static final String ROLE_USER = "ROLE_USER";
	private static final String ROLE_ADMIN = "ROLE_ADMIN";	
		
	public static User getMockedAdminUser() {
		User user = new User(ADMIN_USERNAME, "A".repeat(60), LoginProvider.INTERNAL, ADMIN_GIVEN_NAME, ADMIN_FAMILY_NAME);
		user.addRole(new Role(ROLE_USER));
		user.addRole(new Role(ROLE_ADMIN));
		return user;
	}

}