package svobodavlad.imagesprocessing.parameters;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import svobodavlad.imagesprocessing.jpaentities.LastUploadInfo;
import svobodavlad.imagesprocessing.jpaentities.User;
import svobodavlad.imagesprocessing.security.UserRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class LastUploadInfoService {
	
	private final LastUploadInfoRepository lastUploadInfoRepository;
	private final UserRepository userRepository;
	
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
		if (optLastUploadInfo.isEmpty()) return Optional.empty();
		LastUploadInfo lastUploadInfo = optLastUploadInfo.get().updateLastUploadDateTime();
		return Optional.of(lastUploadInfoRepository.save(lastUploadInfo));
	}

}