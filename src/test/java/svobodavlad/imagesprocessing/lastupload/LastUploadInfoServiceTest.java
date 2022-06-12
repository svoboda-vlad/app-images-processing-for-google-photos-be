package svobodavlad.imagesprocessing.lastupload;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;

import svobodavlad.imagesprocessing.jpaentities.LastUploadInfo;
import svobodavlad.imagesprocessing.jpaentities.User;
import svobodavlad.imagesprocessing.security.UserRepository;
import svobodavlad.imagesprocessing.testutil.UnitTestTemplate;
import svobodavlad.imagesprocessing.util.DateTimeUtil;

@WithMockUser // mocking of SecurityContextHolder
class LastUploadInfoServiceTest extends UnitTestTemplate {
	
	private static final String MOCKED_USER_NAME = "user";

	@MockBean
	private LastUploadInfoRepository lastUploadInfoRepository;
	
	@MockBean
	private UserRepository userRepository;
	
	@MockBean
	private DateTimeUtil dateTimeUtil;
	
	@Autowired
	private LastUploadInfoService lastUploadInfoService;

	@Test
	void getForCurrentUser() {
		User mockedUser = new User(MOCKED_USER_NAME, MOCKED_USER_NAME, MOCKED_USER_NAME);
		LastUploadInfo lastUploadInfo = new LastUploadInfo(Instant.now(), mockedUser);
		
		this.given(userRepository.findByUsername(mockedUser.getUsername())).willReturn(Optional.of(mockedUser));
		this.given(lastUploadInfoRepository.findByUser(mockedUser)).willReturn(Optional.of(lastUploadInfo));
		
		this.assertThat(lastUploadInfoService.getForCurrentUser()).isEqualTo(Optional.of(lastUploadInfo));
	}

	@Test
	void updateForCurrentUserWhenInfoExists() {
		User mockedUser = new User(MOCKED_USER_NAME, MOCKED_USER_NAME, MOCKED_USER_NAME);
		Instant now = Instant.now();
		LastUploadInfo lastUploadInfo = new LastUploadInfo(now, mockedUser);
		
		this.given(userRepository.findByUsername(mockedUser.getUsername())).willReturn(Optional.of(mockedUser));
		this.given(lastUploadInfoRepository.findByUser(mockedUser)).willReturn(Optional.of(lastUploadInfo));
		this.given(lastUploadInfoRepository.save(lastUploadInfo)).willReturn(lastUploadInfo);
		this.given(dateTimeUtil.getCurrentDateTime()).willReturn(now);
		
		Optional<LastUploadInfo> optLastUploadInfoUpdated = lastUploadInfoService.updateForCurrentUser();
		this.assertThat(optLastUploadInfoUpdated).isEqualTo(Optional.of(lastUploadInfo));
	}
	
	@Test
	void updateForCurrentUserWhenInfoDoesNotExist() {
		User mockedUser = new User(MOCKED_USER_NAME, MOCKED_USER_NAME, MOCKED_USER_NAME);
		Instant now = Instant.now();
		LastUploadInfo lastUploadInfo = new LastUploadInfo(now, mockedUser);
		
		this.given(userRepository.findByUsername(mockedUser.getUsername())).willReturn(Optional.of(mockedUser));
		this.given(lastUploadInfoRepository.findByUser(mockedUser)).willReturn(Optional.empty());		
		this.given(lastUploadInfoRepository.save(this.any(LastUploadInfo.class))).willReturn(lastUploadInfo);
		this.given(dateTimeUtil.getCurrentDateTime()).willReturn(now);
				
		this.assertThat(lastUploadInfoService.updateForCurrentUser()).isEqualTo(Optional.of(lastUploadInfo));
	}	
	
}