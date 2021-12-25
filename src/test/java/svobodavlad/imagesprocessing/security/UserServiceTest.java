package svobodavlad.imagesprocessing.security;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Optional;

import javax.persistence.EntityExistsException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;

import svobodavlad.imagesprocessing.jpaentities.Role;
import svobodavlad.imagesprocessing.jpaentities.User;
import svobodavlad.imagesprocessing.parameters.ProcessingParametersUserService;
import svobodavlad.imagesprocessing.testutil.SecurityMockUtil;
import svobodavlad.imagesprocessing.testutil.UnitTestTemplate;

@WithMockUser(username = SecurityMockUtil.DEFAULT_USERNAME) // mocking of SecurityContextHolder
public class UserServiceTest extends UnitTestTemplate {

	private static final String USER_ROLE_NAME = "ROLE_USER";
	private static final String ADMIN_ROLE_NAME = "ROLE_ADMIN";

	@MockBean
	private RoleRepository roleRepository;

	@MockBean
	private UserRepository userRepository;
	
	@MockBean
	private ProcessingParametersUserService parametersService;	

	@Autowired
	private UserService userService;
	
	@Test
	void testRegisterUserNewUser() {
		Role role = new Role(USER_ROLE_NAME);
		User mockedUser = SecurityMockUtil.getMockedDefaultUserInternal();

		this.given(roleRepository.findByName(USER_ROLE_NAME)).willReturn(Optional.of(role));
		this.given(userRepository.findByUsername(mockedUser.getUsername())).willReturn(Optional.empty());
		this.given(userRepository.save(mockedUser)).willReturn(mockedUser);

		this.assertThat(userService.registerUser(mockedUser)).isEqualTo(mockedUser);
		this.verify(parametersService, this.times(1)).setInitialParameters(mockedUser.getUsername());
	}

	@Test
	void testRegisterUserAlreadyExistsException() {
		Role role = new Role(USER_ROLE_NAME);
		User mockedUser = SecurityMockUtil.getMockedDefaultUserInternal();

		this.given(roleRepository.findByName(USER_ROLE_NAME)).willReturn(Optional.of(role));
		this.given(userRepository.findByUsername(mockedUser.getUsername())).willReturn(Optional.of(mockedUser));

		this.assertThatExceptionOfType(EntityExistsException.class).isThrownBy(() -> {
			userService.registerUser(mockedUser);
		});

	}

	@Test
	void testRegisterUserDefaultRoleNotFound() {
		User mockedUser = SecurityMockUtil.getMockedDefaultUserInternal();

		this.given(roleRepository.findByName(USER_ROLE_NAME)).willReturn(Optional.empty());

		this.assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> {
			userService.registerUser(mockedUser);
		});
	}

	@Test
	void testUpdateLastLoginDateTimeFirstLogin() {
		User mockedUser = SecurityMockUtil.getMockedDefaultUserInternal();

		this.given(userRepository.findByUsername(mockedUser.getUsername())).willReturn(Optional.of(mockedUser));
		this.given(userRepository.save(mockedUser)).willReturn(mockedUser);

		Instant minTime = Instant.now();
		
		this.assertThat(userService.updateLastLoginDateTime(mockedUser.getUsername())).isEqualTo(mockedUser);
		this.assertThat(mockedUser.getLastLoginDateTime()).isBetween(minTime, Instant.now());
		this.assertThat(mockedUser.getPreviousLoginDateTime()).isBetween(minTime, Instant.now());
	}
	
	@Test
	void testUpdateLastLoginDateTimeSecondLogin() {
		User mockedUser = SecurityMockUtil.getMockedDefaultUserInternal();
		Instant lastLoginDateTime = LocalDateTime.of(LocalDate.of(2021, 9, 26), LocalTime.of(12, 53)).toInstant(ZoneOffset.UTC);;
		mockedUser.setLastLoginDateTime(lastLoginDateTime);
		mockedUser.setPreviousLoginDateTime(lastLoginDateTime);
		
		this.given(userRepository.findByUsername(mockedUser.getUsername())).willReturn(Optional.of(mockedUser));
		this.given(userRepository.save(mockedUser)).willReturn(mockedUser);

		Instant minTime = Instant.now();
		
		this.assertThat(userService.updateLastLoginDateTime(mockedUser.getUsername())).isEqualTo(mockedUser);
		this.assertThat(mockedUser.getLastLoginDateTime()).isBetween(minTime, Instant.now());
		this.assertThat(mockedUser.getPreviousLoginDateTime()).isEqualTo(lastLoginDateTime);
	}
	
	@Test
	void testUpdateLastLoginDateTimeUserDoesNotExist() {
		String username = "userx";
		this.given(userRepository.findByUsername(username)).willReturn(Optional.empty());
		
		this.assertThat(userService.updateLastLoginDateTime(username)).isNull();
	}

	@Test
	void testUpdateUserOkUserExists() {
		User mockedUser = SecurityMockUtil.getMockedDefaultUserInternal();
		UserInfo mockedUserInfo = mockedUser.toUserInfo();

		this.given(userRepository.findByUsername(mockedUser.getUsername())).willReturn(Optional.of(mockedUser));
		this.given(userRepository.save(mockedUserInfo.toUser(mockedUser))).willReturn(mockedUser);
		this.assertThat(userService.updateCurrentUser(mockedUserInfo)).isEqualTo(mockedUser);
	}

	@Test
	void testRegisterUserNewAdminUser() {
		Role role1 = new Role(USER_ROLE_NAME);
		Role role2 = new Role(ADMIN_ROLE_NAME);
		User mockedUser = SecurityMockUtil.getMockedDefaultUserInternal();

		this.given(roleRepository.findByName(USER_ROLE_NAME)).willReturn(Optional.of(role1));
		this.given(roleRepository.findByName(ADMIN_ROLE_NAME)).willReturn(Optional.of(role2));
		this.given(userRepository.findByUsername(mockedUser.getUsername())).willReturn(Optional.empty());
		this.given(userRepository.save(mockedUser)).willReturn(mockedUser);

		this.assertThat(userService.registerAdminUser(mockedUser)).isEqualTo(mockedUser);
		this.verify(parametersService, this.times(1)).setInitialParameters(mockedUser.getUsername());
	}

	@Test
	void testRegisterAdminUserAdminRoleNotFound() {
		Role role1 = new Role(USER_ROLE_NAME);
		User mockedUser = SecurityMockUtil.getMockedDefaultUserInternal();

		this.given(roleRepository.findByName(USER_ROLE_NAME)).willReturn(Optional.of(role1));
		this.given(userRepository.findByUsername(mockedUser.getUsername())).willReturn(Optional.empty());
		this.given(userRepository.save(mockedUser)).willReturn(mockedUser);
		this.given(roleRepository.findByName(ADMIN_ROLE_NAME)).willReturn(Optional.empty());

		this.assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> {
			userService.registerAdminUser(mockedUser);
		});
	}
	
	@Test
	void testDeleteUserOkUserDeleted() {
		User mockedUser = SecurityMockUtil.getMockedDefaultUserInternal();

		this.given(userRepository.findByUsername(mockedUser.getUsername())).willReturn(Optional.of(mockedUser));		
		userService.deleteCurrentUser();

		this.verify(parametersService, this.times(1)).deleteForCurrentUser();
		this.verify(userRepository, this.times(1)).delete(mockedUser);
		this.verify(userRepository, this.times(1)).flush();		
	}
	
	@Test
	void testGetCurrentUserOkUserExists() {
		User mockedUser = SecurityMockUtil.getMockedDefaultUserInternal();

		this.given(userRepository.findByUsername(mockedUser.getUsername())).willReturn(Optional.of(mockedUser));		
		this.assertThat(userService.getCurrentUser()).isEqualTo(Optional.of(mockedUser));
	}	

}
