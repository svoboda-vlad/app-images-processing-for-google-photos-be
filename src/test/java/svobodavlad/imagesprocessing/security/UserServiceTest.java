package svobodavlad.imagesprocessing.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.BDDMockito.given;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Optional;

import javax.persistence.EntityExistsException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import svobodavlad.imagesprocessing.security.User.LoginProvider;

@SpringBootTest
public class UserServiceTest {

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
		Role role = new Role(0L, USER_ROLE_NAME);
		User user = new User(0L, "user", "A".repeat(60), LoginProvider.INTERNAL, "User", "User", null, null, new ArrayList<UserRoles>());
		user.addRole(role);

		given(roleRepository.findByName(USER_ROLE_NAME)).willReturn(Optional.of(role));
		given(userRepository.findByUsername("user")).willReturn(Optional.empty());
		given(userRepository.save(user)).willReturn(user);

		assertThat(userService.registerUser(user)).isEqualTo(user);
	}

	@Test
	void testRegisterUserAlreadyExistsException() {
		Role role = new Role(0L, USER_ROLE_NAME);
		User user = new User(0L, "user", "A".repeat(60), LoginProvider.INTERNAL, "User", "User", null, null, new ArrayList<UserRoles>());

		given(roleRepository.findByName(USER_ROLE_NAME)).willReturn(Optional.of(role));
		given(userRepository.findByUsername("user")).willReturn(Optional.of(user));

		assertThatExceptionOfType(EntityExistsException.class).isThrownBy(() -> {
			userService.registerUser(user);
		});

	}

	@Test
	void testRegisterUserDefaultRoleNotFound() {
		User user = new User(0L, "user", "A".repeat(60), LoginProvider.INTERNAL, "User", "User", null, null, new ArrayList<UserRoles>());

		given(roleRepository.findByName(USER_ROLE_NAME)).willReturn(Optional.empty());

		assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> {
			userService.registerUser(user);
		});
	}

	@Test
	void testUpdateLastLoginDateTimeFirstLogin() {
		String username = "user";
		User user = new User(0L, username, "A".repeat(60), LoginProvider.INTERNAL, "User", "User", null, null, new ArrayList<UserRoles>());

		given(userRepository.findByUsername(username)).willReturn(Optional.of(user));
		given(userRepository.save(user)).willReturn(user);

		LocalDateTime minTime = LocalDateTime.now();
		
		assertThat(userService.updateLastLoginDateTime(username)).isEqualTo(user);
		assertThat(user.getLastLoginDateTime()).isBetween(minTime, LocalDateTime.now());
		assertThat(user.getPreviousLoginDateTime()).isBetween(minTime, LocalDateTime.now());
	}
	
	@Test
	void testUpdateLastLoginDateTimeSecondLogin() {
		String username = "user";
		LocalDateTime lastLoginDateTime = LocalDateTime.of(LocalDate.of(2021, 9, 26), LocalTime.of(12, 53));
		User user = new User(0L, username, "A".repeat(60), LoginProvider.INTERNAL, "User", "User", lastLoginDateTime, lastLoginDateTime, new ArrayList<UserRoles>());
		
		given(userRepository.findByUsername(username)).willReturn(Optional.of(user));
		given(userRepository.save(user)).willReturn(user);

		LocalDateTime minTime = LocalDateTime.now();
		
		assertThat(userService.updateLastLoginDateTime(username)).isEqualTo(user);
		assertThat(user.getLastLoginDateTime()).isBetween(minTime, LocalDateTime.now());
		assertThat(user.getPreviousLoginDateTime()).isEqualTo(lastLoginDateTime);
	}
	
	@Test
	void testUpdateLastLoginDateTimeUserDoesNotExist() {
		String username = "userx";		
		given(userRepository.findByUsername(username)).willReturn(Optional.empty());
		
		assertThat(userService.updateLastLoginDateTime(username)).isNull();
	}

	@Test
	void testUpdateUserOkUserExists() {
		String username = "user";
		User user = new User(0L, "user", "A".repeat(60), LoginProvider.INTERNAL, "User", "User", null, null, new ArrayList<UserRoles>());
		UserInfo userInfo = new UserInfo("user", "User", "User", null, null, null);

		given(userRepository.findByUsername(username)).willReturn(Optional.of(user));
		given(userRepository.save(userInfo.toUser(user))).willReturn(user);
		assertThat(userService.updateUser(userInfo)).isEqualTo(user);
	}

	@Test
	void testRegisterUserNewAdminUser() {
		Role role1 = new Role(0L, USER_ROLE_NAME);
		Role role2 = new Role(0L, ADMIN_ROLE_NAME);
		User user = new User(0L, "user", "A".repeat(60), LoginProvider.INTERNAL, "User", "User", null, null, new ArrayList<UserRoles>());
		user.addRole(role1);
		user.addRole(role2);

		given(roleRepository.findByName(USER_ROLE_NAME)).willReturn(Optional.of(role1));
		given(roleRepository.findByName(ADMIN_ROLE_NAME)).willReturn(Optional.of(role2));
		given(userRepository.findByUsername("user")).willReturn(Optional.empty());
		given(userRepository.save(user)).willReturn(user);

		assertThat(userService.registerAdminUser(user)).isEqualTo(user);
	}

	@Test
	void testRegisterAdminUserAdminRoleNotFound() {
		User user = new User(0L, "user", "A".repeat(60), LoginProvider.INTERNAL, "User", "User", null, null, new ArrayList<UserRoles>());

		Role role1 = new Role(0L, USER_ROLE_NAME);
		given(roleRepository.findByName(USER_ROLE_NAME)).willReturn(Optional.of(role1));
		given(roleRepository.findByName(ADMIN_ROLE_NAME)).willReturn(Optional.empty());

		assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> {
			userService.registerAdminUser(user);
		});
	}

}
