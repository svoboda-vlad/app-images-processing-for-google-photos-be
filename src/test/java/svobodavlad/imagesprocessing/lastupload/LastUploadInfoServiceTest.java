package svobodavlad.imagesprocessing.lastupload;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.test.context.support.WithMockUser;

import svobodavlad.imagesprocessing.jpaentities.LastUploadInfo;
import svobodavlad.imagesprocessing.jpaentities.User;
import svobodavlad.imagesprocessing.security.UserRepository;
import svobodavlad.imagesprocessing.testutil.UnitTestTemplate;
import svobodavlad.imagesprocessing.util.DateTimeUtil;

@WithMockUser // mocking of SecurityContextHolder
class LastUploadInfoServiceTest extends UnitTestTemplate {
	
	private static final String MOCKED_USER_NAME = "user";

	@Mock
	private LastUploadInfoRepository lastUploadInfoRepository;
	
	@Mock
	private UserRepository userRepository;
	
	@Mock
	private DateTimeUtil dateTimeUtil;
	
	@InjectMocks
	private LastUploadInfoService lastUploadInfoService;

	@Test
	void getForCurrentUser() {
		var mockedUser = new User().setUsername(MOCKED_USER_NAME).setGivenName(MOCKED_USER_NAME).setFamilyName(MOCKED_USER_NAME);
		var lastUploadInfo = new LastUploadInfo().setLastUploadDateTime(Instant.now()).setUser(mockedUser);
		this.given(userRepository.findByUsername(mockedUser.getUsername())).willReturn(Optional.of(mockedUser));
		this.given(lastUploadInfoRepository.findByUser(mockedUser)).willReturn(Optional.of(lastUploadInfo));
		
		this.assertThat(lastUploadInfoService.getForCurrentUser()).isEqualTo(Optional.of(lastUploadInfo));
	}

	@Test
	void updateForCurrentUserWhenInfoExists() {
		var mockedUser = new User().setUsername(MOCKED_USER_NAME).setGivenName(MOCKED_USER_NAME).setFamilyName(MOCKED_USER_NAME);
		var now = Instant.now();
		var lastUploadInfo = new LastUploadInfo().setLastUploadDateTime(now).setUser(mockedUser);
		this.given(userRepository.findByUsername(mockedUser.getUsername())).willReturn(Optional.of(mockedUser));
		this.given(lastUploadInfoRepository.findByUser(mockedUser)).willReturn(Optional.of(lastUploadInfo));
		this.given(lastUploadInfoRepository.save(lastUploadInfo)).willReturn(lastUploadInfo);
		this.given(dateTimeUtil.getCurrentDateTime()).willReturn(now);
		
		var optLastUploadInfoUpdated = lastUploadInfoService.updateForCurrentUser();
		this.assertThat(optLastUploadInfoUpdated).isEqualTo(Optional.of(lastUploadInfo));
	}
	
	@Test
	void updateForCurrentUserWhenInfoDoesNotExist() {
		var mockedUser = new User().setUsername(MOCKED_USER_NAME).setGivenName(MOCKED_USER_NAME).setFamilyName(MOCKED_USER_NAME);
		var now = Instant.now();
		var lastUploadInfo = new LastUploadInfo().setLastUploadDateTime(now).setUser(mockedUser);
		this.given(userRepository.findByUsername(mockedUser.getUsername())).willReturn(Optional.of(mockedUser));
		this.given(lastUploadInfoRepository.findByUser(mockedUser)).willReturn(Optional.empty());		
		this.given(lastUploadInfoRepository.save(this.any(LastUploadInfo.class))).willReturn(lastUploadInfo);
		this.given(dateTimeUtil.getCurrentDateTime()).willReturn(now);
				
		this.assertThat(lastUploadInfoService.updateForCurrentUser()).isEqualTo(Optional.of(lastUploadInfo));
	}	
	
}