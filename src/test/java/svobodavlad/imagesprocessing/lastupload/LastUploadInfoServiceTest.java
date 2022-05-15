package svobodavlad.imagesprocessing.lastupload;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;

import svobodavlad.imagesprocessing.jpaentities.LastUploadInfo;
import svobodavlad.imagesprocessing.jpaentities.User;
import svobodavlad.imagesprocessing.security.UserRegister;
import svobodavlad.imagesprocessing.security.UserRepository;
import svobodavlad.imagesprocessing.testutil.UnitTestTemplate;

@WithMockUser // mocking of SecurityContextHolder
class LastUploadInfoServiceTest extends UnitTestTemplate {
	
	private static final String MOCKED_USER_NAME = "user";

	@MockBean
	private LastUploadInfoRepository lastUploadInfoRepository;
	
	@MockBean
	private UserRepository userRepository;
	
	@Autowired
	private LastUploadInfoService lastUploadInfoService;

	@Test
	void testGetForCurrentUser() {
		User mockedUser = new UserRegister(MOCKED_USER_NAME, MOCKED_USER_NAME, MOCKED_USER_NAME, null).toUser();
		LastUploadInfo lastUploadInfo = new LastUploadInfo(Instant.now(), mockedUser);
		
		this.given(userRepository.findByUsername(mockedUser.getUsername())).willReturn(Optional.of(mockedUser));
		this.given(lastUploadInfoRepository.findByUser(mockedUser)).willReturn(Optional.of(lastUploadInfo));
		
		this.assertThat(lastUploadInfoService.getForCurrentUser()).isEqualTo(Optional.of(lastUploadInfo));
	}

	@Test
	void testUpdateForCurrentUserWhenInfoExists() {
		User mockedUser = new UserRegister(MOCKED_USER_NAME, MOCKED_USER_NAME, MOCKED_USER_NAME, null).toUser();
		LastUploadInfo lastUploadInfo = new LastUploadInfo(Instant.now(), mockedUser);
		
		this.given(userRepository.findByUsername(mockedUser.getUsername())).willReturn(Optional.of(mockedUser));
		this.given(lastUploadInfoRepository.findByUser(mockedUser)).willReturn(Optional.of(lastUploadInfo));
		this.given(lastUploadInfoRepository.save(lastUploadInfo)).willReturn(lastUploadInfo);
		
		Instant minTime = Instant.now();
		Optional<LastUploadInfo> optLastUploadInfoUpdated = lastUploadInfoService.updateForCurrentUser();
		this.assertThat(optLastUploadInfoUpdated).isEqualTo(Optional.of(lastUploadInfo));
		this.assertThat(optLastUploadInfoUpdated.get().getLastUploadDateTime()).isBetween(minTime, Instant.now());	
	}
	
	@Test
	void testUpdateForCurrentUserWhenInfoDoesNotExist() {
		User mockedUser = new UserRegister(MOCKED_USER_NAME, MOCKED_USER_NAME, MOCKED_USER_NAME, null).toUser();
		LastUploadInfo lastUploadInfo = new LastUploadInfo(Instant.now(), mockedUser);
		
		this.given(userRepository.findByUsername(mockedUser.getUsername())).willReturn(Optional.of(mockedUser));
		this.given(lastUploadInfoRepository.findByUser(mockedUser)).willReturn(Optional.empty());		
		this.given(lastUploadInfoRepository.save(this.any(LastUploadInfo.class))).willReturn(lastUploadInfo);
				
		this.assertThat(lastUploadInfoService.updateForCurrentUser()).isEqualTo(Optional.of(lastUploadInfo));
	}	
	
}