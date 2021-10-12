package svobodavlad.imagesprocessing.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import svobodavlad.imagesprocessing.security.RoleRepository;
import svobodavlad.imagesprocessing.security.User;
import svobodavlad.imagesprocessing.security.UserRegister;
import svobodavlad.imagesprocessing.security.UserService;
import svobodavlad.imagesprocessing.testutil.IntegTestTemplate;
import svobodavlad.imagesprocessing.testutil.SecurityMockUtil;

public class UserServiceIT extends IntegTestTemplate {

	@Autowired
	private UserService userService;
	
	@Autowired
	private PasswordEncoder encoder;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Test
	void testRegisterUserNewAdminUser() {
		User mockedUser = SecurityMockUtil.getMockedAdminUser();
		
		UserRegister userRegister = new UserRegister(mockedUser.getUsername(), mockedUser.getPassword(),
				mockedUser.getGivenName(), mockedUser.getFamilyName());
		User user = userRegister.toUserInternal(encoder);
		
		this.assertThat(userService.registerAdminUser(user)).isEqualTo(user);
	}

	@Test
	void testRegisterAdminUserAdminRoleNotFound() {
		User mockedUser = SecurityMockUtil.getMockedAdminUser();
		
		UserRegister userRegister = new UserRegister(mockedUser.getUsername(), mockedUser.getPassword(),
				mockedUser.getGivenName(), mockedUser.getFamilyName());
		User user = userRegister.toUserInternal(encoder);
		
		roleRepository.deleteById(roleRepository.findByName("ROLE_ADMIN").get().getId());

		this.assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> {
			userService.registerAdminUser(user);
		});
	}

}
