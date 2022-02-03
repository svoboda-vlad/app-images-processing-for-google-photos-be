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
import svobodavlad.imagesprocessing.testutil.SecurityMockUtil;
import svobodavlad.imagesprocessing.testutil.UnitTestTemplate;

@WithMockUser(username = SecurityMockUtil.DEFAULT_USERNAME) // mocking of SecurityContextHolder
class LastUploadInfoServiceTest extends UnitTestTemplate {

	@MockBean
	private LastUploadInfoRepository lastUploadInfoRepository;
	
	@MockBean
	private UserRepository userRepository;
	
	@Autowired
	private LastUploadInfoService lastUploadInfoService;

	@Test
	void testGetForCurrentUser() {
		User mockedUser = SecurityMockUtil.getMockedDefaultUserInternal();
		LastUploadInfo lastUploadInfo = new LastUploadInfo(Instant.now(), mockedUser);
		
		this.given(userRepository.findByUsername(mockedUser.getUsername())).willReturn(Optional.of(mockedUser));
		this.given(lastUploadInfoRepository.findByUser(mockedUser)).willReturn(Optional.of(lastUploadInfo));
		
		this.assertThat(lastUploadInfoService.getForCurrentUser()).isEqualTo(Optional.of(lastUploadInfo));
	}

	@Test
	void testUpdateForCurrentUserWhenInfoExists() {
		User mockedUser = SecurityMockUtil.getMockedDefaultUserInternal();
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
		User mockedUser = SecurityMockUtil.getMockedDefaultUserInternal();
		LastUploadInfo lastUploadInfo = new LastUploadInfo(Instant.now(), mockedUser);
		
		this.given(userRepository.findByUsername(mockedUser.getUsername())).willReturn(Optional.of(mockedUser));
		this.given(lastUploadInfoRepository.findByUser(mockedUser)).willReturn(Optional.empty());		
		this.given(lastUploadInfoRepository.save(this.any(LastUploadInfo.class))).willReturn(lastUploadInfo);
				
		this.assertThat(lastUploadInfoService.updateForCurrentUser()).isEqualTo(Optional.of(lastUploadInfo));
	}	
	
}