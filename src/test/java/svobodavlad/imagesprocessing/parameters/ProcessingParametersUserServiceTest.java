package svobodavlad.imagesprocessing.parameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.test.context.support.WithMockUser;

import svobodavlad.imagesprocessing.jpaentities.ProcessingParametersDefault;
import svobodavlad.imagesprocessing.jpaentities.ProcessingParametersUser;
import svobodavlad.imagesprocessing.jpaentities.User;
import svobodavlad.imagesprocessing.security.UserRepository;
import svobodavlad.imagesprocessing.testutil.UnitTestTemplate;

@WithMockUser // mocking of SecurityContextHolder
class ProcessingParametersUserServiceTest extends UnitTestTemplate {
	
	private static final String MOCKED_USER_NAME = "user";

	@Mock
	private ProcessingParametersUserRepository parametersRepository;
	
	@Mock
	private ProcessingParametersDefaultRepository parametersDefaultRepository;
	
	@Mock
	private UserRepository userRepository;
	
	@InjectMocks
	private ProcessingParametersUserService parametersService;
	
	@Test
	void resetToDefaultReturnsDefault() {
		User mockedUser = new User(MOCKED_USER_NAME, MOCKED_USER_NAME, MOCKED_USER_NAME);
		ProcessingParametersUser parameters = new ProcessingParametersUser(1800, 1000, 1000, mockedUser);
		
		this.given(userRepository.findByUsername(mockedUser.getUsername())).willReturn(Optional.of(mockedUser));
		this.given(parametersRepository.findByUser(mockedUser)).willReturn(Optional.of(parameters));
		
		ProcessingParametersDefault parametersDefault = new ProcessingParametersDefault(3600, 1000, 1000);
		this.given(parametersDefaultRepository.findAll()).willReturn(new ArrayList<ProcessingParametersDefault>(List.of(parametersDefault)));
		
		ProcessingParametersUser parametersAfterReset = new ProcessingParametersUser(parametersDefault.getTimeDiffGroup(), parametersDefault.getResizeWidth(), parametersDefault.getResizeHeight(), mockedUser);
		this.given(parametersRepository.save(parametersAfterReset)).willReturn(parametersAfterReset);
		
		this.assertThat(parametersService.resetToDefault()).isEqualTo(parametersAfterReset);
	}
	
	@Test
	void resetToDefaultThrowsError() {
		User mockedUser = new User(MOCKED_USER_NAME, MOCKED_USER_NAME, MOCKED_USER_NAME);
		ProcessingParametersUser parameters = new ProcessingParametersUser(1800, 1000, 1000, mockedUser);
		
		this.given(userRepository.findByUsername(mockedUser.getUsername())).willReturn(Optional.of(mockedUser));
		this.given(parametersRepository.findByUser(mockedUser)).willReturn(Optional.of(parameters));
		
		this.given(parametersDefaultRepository.findAll()).willReturn(new ArrayList<ProcessingParametersDefault>());
		
		this.assertThatExceptionOfType(RuntimeException.class)
		  .isThrownBy(() -> {
			  parametersService.resetToDefault();
		});
	}
	
	@Test
	void resetToDefaultReturnsDefaultWhenParametersUserNotFound() {
		User mockedUser = new User(MOCKED_USER_NAME, MOCKED_USER_NAME, MOCKED_USER_NAME);
		
		this.given(userRepository.findByUsername(mockedUser.getUsername())).willReturn(Optional.of(mockedUser));
		this.given(parametersRepository.findByUser(mockedUser)).willReturn(Optional.empty());
		
		ProcessingParametersDefault parametersDefault = new ProcessingParametersDefault(3600, 1000, 1000);
		this.given(parametersDefaultRepository.findAll()).willReturn(new ArrayList<ProcessingParametersDefault>(List.of(parametersDefault)));
		
		ProcessingParametersUser parametersAfterReset = new ProcessingParametersUser(parametersDefault.getTimeDiffGroup(), parametersDefault.getResizeWidth(), parametersDefault.getResizeHeight(), mockedUser);
		
		this.given(parametersRepository.save(parametersAfterReset)).willReturn(parametersAfterReset);
		
		this.assertThat(parametersService.resetToDefault()).isEqualTo(parametersAfterReset);
	}
	
	@Test
	void setInitialParameters() {
		User mockedUser = new User(MOCKED_USER_NAME, MOCKED_USER_NAME, MOCKED_USER_NAME);
		
		this.given(userRepository.findByUsername(mockedUser.getUsername())).willReturn(Optional.of(mockedUser));
		this.given(parametersRepository.findByUser(mockedUser)).willReturn(Optional.empty());
		
		ProcessingParametersDefault parametersDefault = new ProcessingParametersDefault(3600, 1000, 1000);
		this.given(parametersDefaultRepository.findAll()).willReturn(new ArrayList<ProcessingParametersDefault>(List.of(parametersDefault)));
		
		ProcessingParametersUser initialParameters = new ProcessingParametersUser(parametersDefault.getTimeDiffGroup(), parametersDefault.getResizeWidth(), parametersDefault.getResizeHeight(), mockedUser);
		this.given(parametersRepository.save(initialParameters)).willReturn(initialParameters);
		
		this.assertThat(parametersService.setInitialParameters(mockedUser.getUsername())).isEqualTo(Optional.of(initialParameters));
	}
	
	@Test
	void setInitialParametersThrowsError() {
		User mockedUser = new User(MOCKED_USER_NAME, MOCKED_USER_NAME, MOCKED_USER_NAME);
		
		this.given(userRepository.findByUsername(mockedUser.getUsername())).willReturn(Optional.of(mockedUser));
		this.given(parametersRepository.findByUser(mockedUser)).willReturn(Optional.empty());
		this.given(parametersDefaultRepository.findAll()).willReturn(new ArrayList<ProcessingParametersDefault>());
		
		this.assertThatExceptionOfType(RuntimeException.class)
		  .isThrownBy(() -> {
			  parametersService.setInitialParameters(mockedUser.getUsername());
		});		
	}
	
	@Test
	void testSetInitialParametersDefaultParametersNotFound() {
		User mockedUser = new User(MOCKED_USER_NAME, MOCKED_USER_NAME, MOCKED_USER_NAME);
		
		this.given(userRepository.findByUsername(mockedUser.getUsername())).willReturn(Optional.of(mockedUser));
		
		ProcessingParametersDefault parametersDefault = new ProcessingParametersDefault(3600, 1000, 1000);
		ProcessingParametersUser parameters = new ProcessingParametersUser(parametersDefault.getTimeDiffGroup(), parametersDefault.getResizeWidth(), parametersDefault.getResizeHeight(), mockedUser);
		this.given(parametersRepository.findByUser(mockedUser)).willReturn(Optional.of(parameters));
		
		this.assertThat(parametersService.setInitialParameters(mockedUser.getUsername())).isEqualTo(Optional.empty());	
	}
	
	@Test
	void testDeleteForCurrentUser() {
		User mockedUser = new User(MOCKED_USER_NAME, MOCKED_USER_NAME, MOCKED_USER_NAME);
		ProcessingParametersUser parameters = new ProcessingParametersUser(1800, 1000, 1000, mockedUser);
		parameters.setId(1L);
		
		this.given(userRepository.findByUsername(mockedUser.getUsername())).willReturn(Optional.of(mockedUser));
		this.given(parametersRepository.findByUser(mockedUser)).willReturn(Optional.of(parameters));
		
		parametersService.deleteForCurrentUser();
		
		this.verify(parametersRepository, this.times(1)).delete(parameters);
		this.verify(parametersRepository, this.times(1)).flush();
	}

	@Test
	void testGetForCurrentUser() {
		User mockedUser = new User(MOCKED_USER_NAME, MOCKED_USER_NAME, MOCKED_USER_NAME);
		ProcessingParametersUser parameters = new ProcessingParametersUser(1800, 1000, 1000, mockedUser);
		
		this.given(userRepository.findByUsername(mockedUser.getUsername())).willReturn(Optional.of(mockedUser));
		this.given(parametersRepository.findByUser(mockedUser)).willReturn(Optional.of(parameters));
		
		this.assertThat(parametersService.getForCurrentUser()).isEqualTo(Optional.of(parameters));
	}

	@Test
	void testUpdateForCurrentUser() {
		User mockedUser = new User(MOCKED_USER_NAME, MOCKED_USER_NAME, MOCKED_USER_NAME);
		ProcessingParametersUser parameters = new ProcessingParametersUser(1800, 1000, 1000, mockedUser);
		ProcessingParametersUser parametersUpdated = new ProcessingParametersUser(3600, 1000, 1000, mockedUser);
		ProcessingParametersUserTemplate parametersTemplate = parametersUpdated.toProcessingParametersUserTemplate();
		
		this.given(userRepository.findByUsername(mockedUser.getUsername())).willReturn(Optional.of(mockedUser));
		this.given(parametersRepository.findByUser(mockedUser)).willReturn(Optional.of(parameters));
		this.given(parametersRepository.save(parametersUpdated)).willReturn(parametersUpdated);
		
		this.assertThat(parametersService.updateForCurrentUser(parametersTemplate)).isEqualTo(Optional.of(parametersUpdated));
	}
}