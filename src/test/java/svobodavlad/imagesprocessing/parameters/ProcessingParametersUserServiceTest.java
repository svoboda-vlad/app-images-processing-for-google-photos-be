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
	
	private static final int TIME_DIFF_GROUP = 1800;
	private static final int RESIZE_HEIGHT = 1000;
	private static final int RESIZE_WIDTH = 1000;
	
	private static final int TIME_DIFF_GROUP_UPDATED = 3600;

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
		var mockedUser = new User().setUsername(MOCKED_USER_NAME).setGivenName(MOCKED_USER_NAME).setFamilyName(MOCKED_USER_NAME);
		var parameters = new ProcessingParametersUser().setTimeDiffGroup(TIME_DIFF_GROUP).setResizeHeight(RESIZE_HEIGHT).setResizeWidth(RESIZE_WIDTH).setUser(mockedUser);
		when(userRepository.findByUsername(mockedUser.getUsername())).thenReturn(Optional.of(mockedUser));
		when(parametersRepository.findByUser(mockedUser)).thenReturn(Optional.of(parameters));
		var parametersDefault = new ProcessingParametersDefault().setTimeDiffGroup(TIME_DIFF_GROUP_UPDATED).setResizeHeight(RESIZE_HEIGHT).setResizeWidth(RESIZE_WIDTH);
		when(parametersDefaultRepository.findAll()).thenReturn(new ArrayList<ProcessingParametersDefault>(List.of(parametersDefault)));
		var parametersAfterReset = new ProcessingParametersUser()
				.setTimeDiffGroup(parametersDefault.getTimeDiffGroup())
				.setResizeWidth(parametersDefault.getResizeWidth())
				.setResizeHeight(parametersDefault.getResizeHeight())
				.setUser(mockedUser);
		when(parametersRepository.save(parametersAfterReset)).thenReturn(parametersAfterReset);
		
		assertThat(parametersService.resetToDefault()).isEqualTo(parametersAfterReset);
	}
	
	@Test
	void resetToDefaultThrowsError() {
		var mockedUser = new User().setUsername(MOCKED_USER_NAME).setGivenName(MOCKED_USER_NAME).setFamilyName(MOCKED_USER_NAME);
		var parameters = new ProcessingParametersUser().setTimeDiffGroup(TIME_DIFF_GROUP).setResizeHeight(RESIZE_HEIGHT).setResizeWidth(RESIZE_WIDTH).setUser(mockedUser);
		when(userRepository.findByUsername(mockedUser.getUsername())).thenReturn(Optional.of(mockedUser));
		when(parametersRepository.findByUser(mockedUser)).thenReturn(Optional.of(parameters));
		when(parametersDefaultRepository.findAll()).thenReturn(new ArrayList<ProcessingParametersDefault>());
		
		assertThatExceptionOfType(RuntimeException.class)
		  .isThrownBy(() -> {
			  parametersService.resetToDefault();
		});
	}
	
	@Test
	void resetToDefaultReturnsDefaultWhenParametersUserNotFound() {
		var mockedUser = new User().setUsername(MOCKED_USER_NAME).setGivenName(MOCKED_USER_NAME).setFamilyName(MOCKED_USER_NAME);
		when(userRepository.findByUsername(mockedUser.getUsername())).thenReturn(Optional.of(mockedUser));
		when(parametersRepository.findByUser(mockedUser)).thenReturn(Optional.empty());
		var parametersDefault = new ProcessingParametersDefault().setTimeDiffGroup(TIME_DIFF_GROUP_UPDATED).setResizeHeight(RESIZE_HEIGHT).setResizeWidth(RESIZE_WIDTH);
		when(parametersDefaultRepository.findAll()).thenReturn(new ArrayList<ProcessingParametersDefault>(List.of(parametersDefault)));
		var parametersAfterReset = new ProcessingParametersUser()
				.setTimeDiffGroup(parametersDefault.getTimeDiffGroup())
				.setResizeWidth(parametersDefault.getResizeWidth())
				.setResizeHeight(parametersDefault.getResizeHeight())
				.setUser(mockedUser);
		when(parametersRepository.save(parametersAfterReset)).thenReturn(parametersAfterReset);
		
		assertThat(parametersService.resetToDefault()).isEqualTo(parametersAfterReset);
	}
	
	@Test
	void setInitialParameters() {
		var mockedUser = new User().setUsername(MOCKED_USER_NAME).setGivenName(MOCKED_USER_NAME).setFamilyName(MOCKED_USER_NAME);
		when(userRepository.findByUsername(mockedUser.getUsername())).thenReturn(Optional.of(mockedUser));
		when(parametersRepository.findByUser(mockedUser)).thenReturn(Optional.empty());
		var parametersDefault = new ProcessingParametersDefault().setTimeDiffGroup(TIME_DIFF_GROUP_UPDATED).setResizeHeight(RESIZE_HEIGHT).setResizeWidth(RESIZE_WIDTH);
		when(parametersDefaultRepository.findAll()).thenReturn(new ArrayList<ProcessingParametersDefault>(List.of(parametersDefault)));
		var initialParameters = new ProcessingParametersUser()
				.setTimeDiffGroup(parametersDefault.getTimeDiffGroup())
				.setResizeWidth(parametersDefault.getResizeWidth())
				.setResizeHeight(parametersDefault.getResizeHeight())
				.setUser(mockedUser);		
		when(parametersRepository.save(initialParameters)).thenReturn(initialParameters);
		
		assertThat(parametersService.setInitialParameters(mockedUser.getUsername())).isEqualTo(Optional.of(initialParameters));
	}
	
	@Test
	void setInitialParametersThrowsError() {
		var mockedUser = new User().setUsername(MOCKED_USER_NAME).setGivenName(MOCKED_USER_NAME).setFamilyName(MOCKED_USER_NAME);
		when(userRepository.findByUsername(mockedUser.getUsername())).thenReturn(Optional.of(mockedUser));
		when(parametersRepository.findByUser(mockedUser)).thenReturn(Optional.empty());
		when(parametersDefaultRepository.findAll()).thenReturn(new ArrayList<ProcessingParametersDefault>());
		
		assertThatExceptionOfType(RuntimeException.class)
		  .isThrownBy(() -> {
			  parametersService.setInitialParameters(mockedUser.getUsername());
		});		
	}
	
	@Test
	void testSetInitialParametersDefaultParametersNotFound() {
		var mockedUser = new User().setUsername(MOCKED_USER_NAME).setGivenName(MOCKED_USER_NAME).setFamilyName(MOCKED_USER_NAME);
		when(userRepository.findByUsername(mockedUser.getUsername())).thenReturn(Optional.of(mockedUser));
		var parametersDefault = new ProcessingParametersDefault().setTimeDiffGroup(TIME_DIFF_GROUP_UPDATED).setResizeHeight(RESIZE_HEIGHT).setResizeWidth(RESIZE_WIDTH);
		var parameters = new ProcessingParametersUser()
				.setTimeDiffGroup(parametersDefault.getTimeDiffGroup())
				.setResizeWidth(parametersDefault.getResizeWidth())
				.setResizeHeight(parametersDefault.getResizeHeight())
				.setUser(mockedUser);
		when(parametersRepository.findByUser(mockedUser)).thenReturn(Optional.of(parameters));
		
		assertThat(parametersService.setInitialParameters(mockedUser.getUsername())).isEqualTo(Optional.empty());	
	}
	
	@Test
	void testDeleteForCurrentUser() {
		var mockedUser = new User().setUsername(MOCKED_USER_NAME).setGivenName(MOCKED_USER_NAME).setFamilyName(MOCKED_USER_NAME);
		var parameters = new ProcessingParametersUser().setTimeDiffGroup(TIME_DIFF_GROUP).setResizeHeight(RESIZE_HEIGHT).setResizeWidth(RESIZE_WIDTH).setUser(mockedUser);
		parameters.setId(1L);
		when(userRepository.findByUsername(mockedUser.getUsername())).thenReturn(Optional.of(mockedUser));
		when(parametersRepository.findByUser(mockedUser)).thenReturn(Optional.of(parameters));
		parametersService.deleteForCurrentUser();
		
		verify(parametersRepository, times(1)).delete(parameters);
		verify(parametersRepository, times(1)).flush();
	}

	@Test
	void testGetForCurrentUser() {
		var mockedUser = new User().setUsername(MOCKED_USER_NAME).setGivenName(MOCKED_USER_NAME).setFamilyName(MOCKED_USER_NAME);
		var parameters = new ProcessingParametersUser().setTimeDiffGroup(TIME_DIFF_GROUP).setResizeHeight(RESIZE_HEIGHT).setResizeWidth(RESIZE_WIDTH).setUser(mockedUser);
		when(userRepository.findByUsername(mockedUser.getUsername())).thenReturn(Optional.of(mockedUser));
		when(parametersRepository.findByUser(mockedUser)).thenReturn(Optional.of(parameters));
		
		assertThat(parametersService.getForCurrentUser()).isEqualTo(Optional.of(parameters));
	}

	@Test
	void testUpdateForCurrentUser() {
		var mockedUser = new User().setUsername(MOCKED_USER_NAME).setGivenName(MOCKED_USER_NAME).setFamilyName(MOCKED_USER_NAME);
		var parameters = new ProcessingParametersUser().setTimeDiffGroup(TIME_DIFF_GROUP).setResizeHeight(RESIZE_HEIGHT).setResizeWidth(RESIZE_WIDTH).setUser(mockedUser);
		var parametersUpdated = new ProcessingParametersUser().setTimeDiffGroup(TIME_DIFF_GROUP_UPDATED).setResizeHeight(RESIZE_HEIGHT).setResizeWidth(RESIZE_WIDTH).setUser(mockedUser);		
		var parametersTemplate = parametersUpdated.toProcessingParametersUserTemplate();
		when(userRepository.findByUsername(mockedUser.getUsername())).thenReturn(Optional.of(mockedUser));
		when(parametersRepository.findByUser(mockedUser)).thenReturn(Optional.of(parameters));
		when(parametersRepository.save(parametersUpdated)).thenReturn(parametersUpdated);
		
		assertThat(parametersService.updateForCurrentUser(parametersTemplate)).isEqualTo(Optional.of(parametersUpdated));
	}
}