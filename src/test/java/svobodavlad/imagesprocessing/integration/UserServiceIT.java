package svobodavlad.imagesprocessing.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import svobodavlad.imagesprocessing.jpaentities.User;
import svobodavlad.imagesprocessing.security.RoleRepository;
import svobodavlad.imagesprocessing.security.UserService;
import svobodavlad.imagesprocessing.testutil.IntegTestTemplate;

public class UserServiceIT extends IntegTestTemplate {
	
	private static final String MOCKED_USER_NAME = "admin";

	@Autowired
	private UserService userService;
		
	@Autowired
	private RoleRepository roleRepository;
	
	@Test
	void registerUserNewAdminUser() {
		User user = new User(MOCKED_USER_NAME, MOCKED_USER_NAME, MOCKED_USER_NAME);
		this.assertThat(userService.registerAdminUser(user)).isEqualTo(user);
	}

	@Test
	void registerAdminUserAdminRoleNotFound() {
		User user = new User(MOCKED_USER_NAME, MOCKED_USER_NAME, MOCKED_USER_NAME);
		
		roleRepository.delete(roleRepository.findByName("ROLE_ADMIN").get());

		this.assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> {
			userService.registerAdminUser(user);
		});
	}

}