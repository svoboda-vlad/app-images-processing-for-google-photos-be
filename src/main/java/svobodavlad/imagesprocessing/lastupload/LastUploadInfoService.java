package svobodavlad.imagesprocessing.lastupload;

import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import svobodavlad.imagesprocessing.jpaentities.LastUploadInfo;
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
		var authentication = SecurityContextHolder.getContext().getAuthentication();
		var optUser = userRepository.findByUsername(authentication.getName());
		if (optUser.isEmpty()) return Optional.empty();
		var optLastUploadInfo = lastUploadInfoRepository.findByUser(optUser.get());
		if (optLastUploadInfo.isEmpty()) return Optional.empty();
		return optLastUploadInfo;
	}
	
	public Optional<LastUploadInfo> updateForCurrentUser() {		
		var authentication = SecurityContextHolder.getContext().getAuthentication();
		var optUser = userRepository.findByUsername(authentication.getName());
		if (optUser.isEmpty()) return Optional.empty();
		var optLastUploadInfo = lastUploadInfoRepository.findByUser(optUser.get());
		if (optLastUploadInfo.isPresent()) {
			var lastUploadInfo = optLastUploadInfo.get().updateLastUploadDateTime(dateTimeUtil.getCurrentDateTime());
			return Optional.of(lastUploadInfoRepository.save(lastUploadInfo));			
		}
		var lastUploadInfo = new LastUploadInfo().setUser(optUser.get());
		lastUploadInfo.updateLastUploadDateTime(dateTimeUtil.getCurrentDateTime());
		return Optional.of(lastUploadInfoRepository.save(lastUploadInfo));
	}

}