package svobodavlad.imagesprocessing.security;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import javax.persistence.EntityExistsException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import svobodavlad.imagesprocessing.security.User.LoginProvider;
import svobodavlad.imagesprocessing.testutil.UnitTestTemplate;

public class UserServiceTest extends UnitTestTemplate {

	private static final String USER_ROLE_NAME = "ROLE_USER";
	private static final String ADMIN_ROLE_NAME = "ROLE_ADMIN";

	@MockBean
	private RoleRepository roleRepository;

	@MockBean
	private UserRepository userRepository;

	@Autowired
	private UserService userService;

	@Test
	void testRegisterUserNewUser() {
		Role role = new Role(USER_ROLE_NAME);
		User user = new User("user", "A".repeat(60), LoginProvider.INTERNAL, "User", "User", null, null);
		user.addRole(role);

		this.given(roleRepository.findByName(USER_ROLE_NAME)).willReturn(Optional.of(role));
		this.given(userRepository.findByUsername("user")).willReturn(Optional.empty());
		this.given(userRepository.save(user)).willReturn(user);

		this.assertThat(userService.registerUser(user)).isEqualTo(user);
	}

	@Test
	void testRegisterUserAlreadyExistsException() {
		Role role = new Role(USER_ROLE_NAME);
		User user = new User("user", "A".repeat(60), LoginProvider.INTERNAL, "User", "User", null, null);

		this.given(roleRepository.findByName(USER_ROLE_NAME)).willReturn(Optional.of(role));
		this.given(userRepository.findByUsername("user")).willReturn(Optional.of(user));

		this.assertThatExceptionOfType(EntityExistsException.class).isThrownBy(() -> {
			userService.registerUser(user);
		});

	}

	@Test
	void testRegisterUserDefaultRoleNotFound() {
		User user = new User("user", "A".repeat(60), LoginProvider.INTERNAL, "User", "User", null, null);

		this.given(roleRepository.findByName(USER_ROLE_NAME)).willReturn(Optional.empty());

		this.assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> {
			userService.registerUser(user);
		});
	}

	@Test
	void testUpdateLastLoginDateTimeFirstLogin() {
		String username = "user";
		User user = new User(username, "A".repeat(60), LoginProvider.INTERNAL, "User", "User", null, null);

		this.given(userRepository.findByUsername(username)).willReturn(Optional.of(user));
		this.given(userRepository.save(user)).willReturn(user);

		LocalDateTime minTime = LocalDateTime.now();
		
		this.assertThat(userService.updateLastLoginDateTime(username)).isEqualTo(user);
		this.assertThat(user.getLastLoginDateTime()).isBetween(minTime, LocalDateTime.now());
		this.assertThat(user.getPreviousLoginDateTime()).isBetween(minTime, LocalDateTime.now());
	}
	
	@Test
	void testUpdateLastLoginDateTimeSecondLogin() {
		String username = "user";
		LocalDateTime lastLoginDateTime = LocalDateTime.of(LocalDate.of(2021, 9, 26), LocalTime.of(12, 53));
		User user = new User(username, "A".repeat(60), LoginProvider.INTERNAL, "User", "User", lastLoginDateTime, lastLoginDateTime);
		
		this.given(userRepository.findByUsername(username)).willReturn(Optional.of(user));
		this.given(userRepository.save(user)).willReturn(user);

		LocalDateTime minTime = LocalDateTime.now();
		
		this.assertThat(userService.updateLastLoginDateTime(username)).isEqualTo(user);
		this.assertThat(user.getLastLoginDateTime()).isBetween(minTime, LocalDateTime.now());
		this.assertThat(user.getPreviousLoginDateTime()).isEqualTo(lastLoginDateTime);
	}
	
	@Test
	void testUpdateLastLoginDateTimeUserDoesNotExist() {
		String username = "userx";		
		this.given(userRepository.findByUsername(username)).willReturn(Optional.empty());
		
		this.assertThat(userService.updateLastLoginDateTime(username)).isNull();
	}

	@Test
	void testUpdateUserOkUserExists() {
		String username = "user";
		User user = new User("user", "A".repeat(60), LoginProvider.INTERNAL, "User", "User", null, null);
		UserInfo userInfo = new UserInfo("user", "User", "User", null, null, null);

		this.given(userRepository.findByUsername(username)).willReturn(Optional.of(user));
		this.given(userRepository.save(userInfo.toUser(user))).willReturn(user);
		this.assertThat(userService.updateUser(userInfo)).isEqualTo(user);
	}

	@Test
	void testRegisterUserNewAdminUser() {
		Role role1 = new Role(USER_ROLE_NAME);
		Role role2 = new Role(ADMIN_ROLE_NAME);
		User user = new User("user", "A".repeat(60), LoginProvider.INTERNAL, "User", "User", null, null);
		user.addRole(role1);
		user.addRole(role2);

		this.given(roleRepository.findByName(USER_ROLE_NAME)).willReturn(Optional.of(role1));
		this.given(roleRepository.findByName(ADMIN_ROLE_NAME)).willReturn(Optional.of(role2));
		this.given(userRepository.findByUsername("user")).willReturn(Optional.empty());
		this.given(userRepository.save(user)).willReturn(user);

		this.assertThat(userService.registerAdminUser(user)).isEqualTo(user);
	}

	@Test
	void testRegisterAdminUserAdminRoleNotFound() {
		User user = new User("user", "A".repeat(60), LoginProvider.INTERNAL, "User", "User", null, null);

		Role role1 = new Role(USER_ROLE_NAME);
		this.given(roleRepository.findByName(USER_ROLE_NAME)).willReturn(Optional.of(role1));
		this.given(roleRepository.findByName(ADMIN_ROLE_NAME)).willReturn(Optional.empty());

		this.assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> {
			userService.registerAdminUser(user);
		});
	}

}
