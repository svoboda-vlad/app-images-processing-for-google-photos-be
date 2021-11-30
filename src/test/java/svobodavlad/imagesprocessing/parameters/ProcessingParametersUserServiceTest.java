package svobodavlad.imagesprocessing.parameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;

import svobodavlad.imagesprocessing.jpaentities.ProcessingParametersDefault;
import svobodavlad.imagesprocessing.jpaentities.ProcessingParametersUser;
import svobodavlad.imagesprocessing.jpaentities.User;
import svobodavlad.imagesprocessing.security.UserRepository;
import svobodavlad.imagesprocessing.testutil.SecurityMockUtil;
import svobodavlad.imagesprocessing.testutil.UnitTestTemplate;

@WithMockUser(username = SecurityMockUtil.DEFAULT_USERNAME) // mocking of SecurityContextHolder
class ProcessingParametersUserServiceTest extends UnitTestTemplate {

	@MockBean
	private ProcessingParametersUserRepository parametersRepository;
	
	@MockBean
	private ProcessingParametersDefaultRepository parametersDefaultRepository;
	
	@MockBean
	private UserRepository userRepository;
	
	@Autowired
	private ProcessingParametersUserService parametersService;
	
	@Test
	void testResetToDefaultReturnsDefault() {
		User mockedUser = SecurityMockUtil.getMockedDefaultUserInternal();
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
	void testResetToDefaultThrowsError() {
		User mockedUser = SecurityMockUtil.getMockedDefaultUserInternal();
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
	void testResetToDefaultReturnsDefaultWhenParametersUserNotFound() {
		User mockedUser = SecurityMockUtil.getMockedDefaultUserInternal();
		
		this.given(userRepository.findByUsername(mockedUser.getUsername())).willReturn(Optional.of(mockedUser));
		this.given(parametersRepository.findByUser(mockedUser)).willReturn(Optional.empty());
		
		ProcessingParametersDefault parametersDefault = new ProcessingParametersDefault(3600, 1000, 1000);
		this.given(parametersDefaultRepository.findAll()).willReturn(new ArrayList<ProcessingParametersDefault>(List.of(parametersDefault)));
		
		ProcessingParametersUser parametersAfterReset = new ProcessingParametersUser(parametersDefault.getTimeDiffGroup(), parametersDefault.getResizeWidth(), parametersDefault.getResizeHeight(), mockedUser);
		
		this.given(parametersRepository.save(parametersAfterReset)).willReturn(parametersAfterReset);
		
		this.assertThat(parametersService.resetToDefault()).isEqualTo(parametersAfterReset);
	}
	
	@Test
	void testSetInitialParameters() {
		User mockedUser = SecurityMockUtil.getMockedDefaultUserInternal();
		
		this.given(userRepository.findByUsername(mockedUser.getUsername())).willReturn(Optional.of(mockedUser));
		this.given(parametersRepository.findByUser(mockedUser)).willReturn(Optional.empty());
		
		ProcessingParametersDefault parametersDefault = new ProcessingParametersDefault(3600, 1000, 1000);
		this.given(parametersDefaultRepository.findAll()).willReturn(new ArrayList<ProcessingParametersDefault>(List.of(parametersDefault)));
		
		ProcessingParametersUser initialParameters = new ProcessingParametersUser(parametersDefault.getTimeDiffGroup(), parametersDefault.getResizeWidth(), parametersDefault.getResizeHeight(), mockedUser);
		this.given(parametersRepository.save(initialParameters)).willReturn(initialParameters);
		
		this.assertThat(parametersService.setInitialParameters(mockedUser.getUsername())).isEqualTo(initialParameters);
	}
	
	@Test
	void testSetInitialParametersThrowsError() {
		User mockedUser = SecurityMockUtil.getMockedDefaultUserInternal();
		
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
		User mockedUser = SecurityMockUtil.getMockedDefaultUserInternal();
		
		this.given(userRepository.findByUsername(mockedUser.getUsername())).willReturn(Optional.of(mockedUser));
		
		ProcessingParametersDefault parametersDefault = new ProcessingParametersDefault(3600, 1000, 1000);
		ProcessingParametersUser parameters = new ProcessingParametersUser(parametersDefault.getTimeDiffGroup(), parametersDefault.getResizeWidth(), parametersDefault.getResizeHeight(), mockedUser);
		this.given(parametersRepository.findByUser(mockedUser)).willReturn(Optional.of(parameters));
		
		this.assertThat(parametersService.setInitialParameters(mockedUser.getUsername())).isNull();	
	}
	
	@Test
	void testDeleteReturnsDefault() {
		User mockedUser = SecurityMockUtil.getMockedDefaultUserInternal();
		ProcessingParametersUser parameters = new ProcessingParametersUser(1800, 1000, 1000, mockedUser);
		parameters.setId(1L);
		
		this.given(userRepository.findByUsername(mockedUser.getUsername())).willReturn(Optional.of(mockedUser));
		this.given(parametersRepository.findByUser(mockedUser)).willReturn(Optional.of(parameters));
		
		parametersService.deleteForCurrentUser();
		
		this.verify(parametersRepository, this.times(1)).deleteById(parameters.getId());
		this.verify(parametersRepository, this.times(1)).flush();		
	}	
}