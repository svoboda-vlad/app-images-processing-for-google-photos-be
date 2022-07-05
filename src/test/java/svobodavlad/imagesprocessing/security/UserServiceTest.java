package svobodavlad.imagesprocessing.security;

import java.time.Instant;
import java.util.Optional;

import javax.persistence.EntityExistsException;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.test.context.support.WithMockUser;

import svobodavlad.imagesprocessing.jpaentities.User;
import svobodavlad.imagesprocessing.parameters.ProcessingParametersUserService;
import svobodavlad.imagesprocessing.testutil.UnitTestTemplate;
import svobodavlad.imagesprocessing.util.DateTimeUtil;

@WithMockUser // mocking of SecurityContextHolder
public class UserServiceTest extends UnitTestTemplate {

	private static final String DEFAULT_USERNAME = "user";

	@Mock
	private UserRepository userRepository;
	@Mock
	private ProcessingParametersUserService parametersService;
	@Mock
	private DateTimeUtil dateTimeUtil;
	@InjectMocks
	private UserService userService;
	
	@Test
	void registerUserNewUser() {
		var mockedUser = new User().setUsername(DEFAULT_USERNAME).setGivenName(DEFAULT_USERNAME).setFamilyName(DEFAULT_USERNAME);

		when(userRepository.findByUsername(DEFAULT_USERNAME)).thenReturn(Optional.empty());
		when(userRepository.save(mockedUser)).thenReturn(mockedUser);

		assertThat(userService.registerUser(mockedUser)).isEqualTo(mockedUser);
		verify(parametersService, times(1)).setInitialParameters(DEFAULT_USERNAME);
	}

	@Test
	void registerUserAlreadyExistsException() {
		var mockedUser = new User().setUsername(DEFAULT_USERNAME).setGivenName(DEFAULT_USERNAME).setFamilyName(DEFAULT_USERNAME);

		when(userRepository.findByUsername(DEFAULT_USERNAME)).thenReturn(Optional.of(mockedUser));
		assertThatExceptionOfType(EntityExistsException.class).isThrownBy(() -> {
			userService.registerUser(mockedUser);
		});
	}

	@Test
	void registerUserDefaultRoleNotFound() {
		var mockedUser = new User().setUsername(DEFAULT_USERNAME).setGivenName(DEFAULT_USERNAME).setFamilyName(DEFAULT_USERNAME);

		assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> {
			userService.registerUser(mockedUser);
		});
	}
	
	@Test
	void deleteUserOkUserDeleted() {
		var mockedUser = new User().setUsername(DEFAULT_USERNAME).setGivenName(DEFAULT_USERNAME).setFamilyName(DEFAULT_USERNAME);
		when(userRepository.findByUsername(DEFAULT_USERNAME)).thenReturn(Optional.of(mockedUser));		
		userService.deleteCurrentUser();

		verify(parametersService, times(1)).deleteForCurrentUser();
		verify(userRepository, times(1)).delete(mockedUser);
		verify(userRepository, times(1)).flush();		
	}
	
	@Test
	void getCurrentUserOkUserExists() {
		var mockedUser = new User().setUsername(DEFAULT_USERNAME).setGivenName(DEFAULT_USERNAME).setFamilyName(DEFAULT_USERNAME);
		when(userRepository.findByUsername(DEFAULT_USERNAME)).thenReturn(Optional.of(mockedUser));		
		when(userRepository.save(mockedUser)).thenReturn(mockedUser);
		
		assertThat(userService.getCurrentUser()).isEqualTo(Optional.of(mockedUser));
	}
	
	@Test
	void getCurrentUserOkUserDoesNotExists() {
		var now = Instant.now();
		var mockedUser = new User().setUsername(DEFAULT_USERNAME).setGivenName(DEFAULT_USERNAME).setFamilyName(DEFAULT_USERNAME);
		mockedUser.setEmail(DEFAULT_USERNAME);
		when(userRepository.findByUsername(DEFAULT_USERNAME))
		.thenReturn(Optional.empty())
		.thenReturn(Optional.empty())
		.thenReturn(Optional.of(mockedUser));
		when(dateTimeUtil.getCurrentDateTime()).thenReturn(now);
		when(userRepository.save(mockedUser)).thenReturn(mockedUser);		

		assertThat(userService.getCurrentUser()).isEqualTo(Optional.of(mockedUser));
	}
	
}
