package svobodavlad.imagesprocessing.testutil;

import java.util.ArrayList;

import svobodavlad.imagesprocessing.security.Role;
import svobodavlad.imagesprocessing.security.User;
import svobodavlad.imagesprocessing.security.User.LoginProvider;
import svobodavlad.imagesprocessing.security.UserRoles;

public class SecurityMockUtil {
	
	private static final String ADMIN_USERNAME = "admin";
	private static final String ADMIN_GIVEN_NAME = "Administrator";
	private static final String ADMIN_FAMILY_NAME = "Administrator";
	private static final String USER_USERNAME = "user1";
	private static final String USER_GIVEN_NAME = "User 1";
	private static final String USER_FAMILY_NAME = "User 1";	
	private static final String ROLE_USER = "ROLE_USER";
	private static final String ROLE_ADMIN = "ROLE_ADMIN";	
		
	public static User getMockedAdminUser() {
		User user = new User(0L, ADMIN_USERNAME, "A".repeat(60), LoginProvider.INTERNAL, ADMIN_GIVEN_NAME, ADMIN_FAMILY_NAME, null, null, new ArrayList<UserRoles>());
		user.addRole(new Role(0L, ROLE_USER));
		user.addRole(new Role(0L, ROLE_ADMIN));
		return user;
	}
	
	public static User getMockedDefaultUser() {
		User user = new User(0L, USER_USERNAME, "A".repeat(60), LoginProvider.INTERNAL, USER_GIVEN_NAME, USER_FAMILY_NAME, null, null, new ArrayList<UserRoles>());
		user.addRole(new Role(0L, ROLE_USER));
		return user;
	}	

}
