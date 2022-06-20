package svobodavlad.imagesprocessing.lastupload;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import svobodavlad.imagesprocessing.jpaentities.LastUploadInfo;
import svobodavlad.imagesprocessing.jpaentities.User;
import svobodavlad.imagesprocessing.security.UserRepository;
import svobodavlad.imagesprocessing.util.DateTimeUtil;

@Service
@Transactional
@RequiredArgsConstructor
public class LastUploadInfoService {
	
	private final LastUploadInfoRepository lastUploadInfoRepository;
	private final UserRepository userRepository;
	private final DateTimeUtil dateTimeUtil;
	
	public Optional<LastUploadInfo> getForCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Optional<User> optUser = userRepository.findByUsername(authentication.getName());
		if (optUser.isEmpty()) return Optional.empty();
		Optional<LastUploadInfo> optLastUploadInfo = lastUploadInfoRepository.findByUser(optUser.get());
		if (optLastUploadInfo.isEmpty()) return Optional.empty();
		return optLastUploadInfo;
	}
	
	public Optional<LastUploadInfo> updateForCurrentUser() {		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Optional<User> optUser = userRepository.findByUsername(authentication.getName());
		if (optUser.isEmpty()) return Optional.empty();
		Optional<LastUploadInfo> optLastUploadInfo = lastUploadInfoRepository.findByUser(optUser.get());
		if (optLastUploadInfo.isPresent()) {
			LastUploadInfo lastUploadInfo = optLastUploadInfo.get().updateLastUploadDateTime(dateTimeUtil.getCurrentDateTime());
			return Optional.of(lastUploadInfoRepository.save(lastUploadInfo));			
		}
		LastUploadInfo lastUploadInfo = new LastUploadInfo().setUser(optUser.get());
		lastUploadInfo.updateLastUploadDateTime(dateTimeUtil.getCurrentDateTime());
		return Optional.of(lastUploadInfoRepository.save(lastUploadInfo));
	}

}