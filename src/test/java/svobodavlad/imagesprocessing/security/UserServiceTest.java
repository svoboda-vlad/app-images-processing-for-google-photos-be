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
		User mockedUser = new User().setUsername(DEFAULT_USERNAME).setGivenName(DEFAULT_USERNAME).setFamilyName(DEFAULT_USERNAME);

		this.given(userRepository.findByUsername(DEFAULT_USERNAME)).willReturn(Optional.empty());
		this.given(userRepository.save(mockedUser)).willReturn(mockedUser);

		this.assertThat(userService.registerUser(mockedUser)).isEqualTo(mockedUser);
		this.verify(parametersService, this.times(1)).setInitialParameters(DEFAULT_USERNAME);
	}

	@Test
	void registerUserAlreadyExistsException() {
		User mockedUser = new User().setUsername(DEFAULT_USERNAME).setGivenName(DEFAULT_USERNAME).setFamilyName(DEFAULT_USERNAME);

		this.given(userRepository.findByUsername(DEFAULT_USERNAME)).willReturn(Optional.of(mockedUser));
		this.assertThatExceptionOfType(EntityExistsException.class).isThrownBy(() -> {
			userService.registerUser(mockedUser);
		});
	}

	@Test
	void registerUserDefaultRoleNotFound() {
		User mockedUser = new User().setUsername(DEFAULT_USERNAME).setGivenName(DEFAULT_USERNAME).setFamilyName(DEFAULT_USERNAME);

		this.assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> {
			userService.registerUser(mockedUser);
		});
	}
	
	@Test
	void deleteUserOkUserDeleted() {
		User mockedUser = new User().setUsername(DEFAULT_USERNAME).setGivenName(DEFAULT_USERNAME).setFamilyName(DEFAULT_USERNAME);

		this.given(userRepository.findByUsername(DEFAULT_USERNAME)).willReturn(Optional.of(mockedUser));		
		userService.deleteCurrentUser();

		this.verify(parametersService, this.times(1)).deleteForCurrentUser();
		this.verify(userRepository, this.times(1)).delete(mockedUser);
		this.verify(userRepository, this.times(1)).flush();		
	}
	
	@Test
	void getCurrentUserOkUserExists() {
		User mockedUser = new User().setUsername(DEFAULT_USERNAME).setGivenName(DEFAULT_USERNAME).setFamilyName(DEFAULT_USERNAME);

		this.given(userRepository.findByUsername(DEFAULT_USERNAME)).willReturn(Optional.of(mockedUser));		
		this.given(userRepository.save(mockedUser)).willReturn(mockedUser);
		
		this.assertThat(userService.getCurrentUser()).isEqualTo(Optional.of(mockedUser));
	}
	
	@Test
	void getCurrentUserOkUserDoesNotExists() {
		Instant now = Instant.now();
		User mockedUser = new User().setUsername(DEFAULT_USERNAME).setGivenName(DEFAULT_USERNAME).setFamilyName(DEFAULT_USERNAME);
		mockedUser.setEmail(DEFAULT_USERNAME);

		this.given(userRepository.findByUsername(DEFAULT_USERNAME))
		.willReturn(Optional.empty())
		.willReturn(Optional.empty())
		.willReturn(Optional.of(mockedUser));
		this.given(dateTimeUtil.getCurrentDateTime()).willReturn(now);
		this.given(userRepository.save(mockedUser)).willReturn(mockedUser);		
		
		this.assertThat(userService.getCurrentUser()).isEqualTo(Optional.of(mockedUser));
	}
	
}
