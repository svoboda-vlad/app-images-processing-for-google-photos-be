package svobodavlad.imagesprocessing.parameters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.BDDMockito.given;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;

import svobodavlad.imagesprocessing.security.User;
import svobodavlad.imagesprocessing.security.UserRepository;
import svobodavlad.imagesprocessing.testutil.SecurityMockUtil;

@SpringBootTest
@WithMockUser(username = "user1")
class ProcessingParametersUserServiceTest {

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
		User mockedUser = SecurityMockUtil.getMockedDefaultUser();
		ProcessingParametersUser parameters = new ProcessingParametersUser(0L, 1800, 1000, 1000, mockedUser);
		
		given(userRepository.findByUsername(mockedUser.getUsername())).willReturn(Optional.of(mockedUser));
		given(parametersRepository.findByUser(mockedUser)).willReturn(Optional.of(parameters));
		
		ProcessingParametersDefault parametersDefault = new ProcessingParametersDefault(0L, 3600, 1000, 1000);
		given(parametersDefaultRepository.findAll()).willReturn(new ArrayList<ProcessingParametersDefault>(List.of(parametersDefault)));
		
		ProcessingParametersUser parametersAfterReset = new ProcessingParametersUser(0L, parametersDefault.getTimeDiffGroup(), parametersDefault.getResizeWidth(), parametersDefault.getResizeHeight(), mockedUser);
		given(parametersRepository.save(parametersAfterReset)).willReturn(parametersAfterReset);
		
		assertThat(parametersService.resetToDefault()).isEqualTo(parametersAfterReset);
	}
	
	@Test
	void testResetToDefaultThrowsError() {
		User mockedUser = SecurityMockUtil.getMockedDefaultUser();
		ProcessingParametersUser parameters = new ProcessingParametersUser(0L, 1800, 1000, 1000, mockedUser);
		
		given(userRepository.findByUsername(mockedUser.getUsername())).willReturn(Optional.of(mockedUser));
		given(parametersRepository.findByUser(mockedUser)).willReturn(Optional.of(parameters));
		
		given(parametersDefaultRepository.findAll()).willReturn(new ArrayList<ProcessingParametersDefault>());
		
		assertThatExceptionOfType(RuntimeException.class)
		  .isThrownBy(() -> {
			  parametersService.resetToDefault();
		});
	}
	
	@Test
	void testResetToDefaultReturnsDefaultWhenParametersUserNotFound() {
		User mockedUser = SecurityMockUtil.getMockedDefaultUser();
		
		given(userRepository.findByUsername(mockedUser.getUsername())).willReturn(Optional.of(mockedUser));
		given(parametersRepository.findByUser(mockedUser)).willReturn(Optional.empty());
		
		ProcessingParametersDefault parametersDefault = new ProcessingParametersDefault(0L, 3600, 1000, 1000);
		given(parametersDefaultRepository.findAll()).willReturn(new ArrayList<ProcessingParametersDefault>(List.of(parametersDefault)));		
		
		ProcessingParametersUser parametersAfterReset = new ProcessingParametersUser(0L, parametersDefault.getTimeDiffGroup(), parametersDefault.getResizeWidth(), parametersDefault.getResizeHeight(), mockedUser);		
		
		given(parametersRepository.save(parametersAfterReset)).willReturn(parametersAfterReset);
		
		assertThat(parametersService.resetToDefault()).isEqualTo(parametersAfterReset);
	}	

}