package svobodavlad.imagesprocessing.parameters;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import svobodavlad.imagesprocessing.jpaentities.LastUploadInfo;

@RestController
@RequiredArgsConstructor
@Transactional
public class LastUploadInfoController {

	private static final String LAST_UPLOAD_INFO_URL = "/last-upload-info";
	private static final String LAST_UPLOAD_INFO_UPDATE_URL = "/last-upload-info-update";
	private final LastUploadInfoService lastUploadInfoService;

	@Operation(security = { @SecurityRequirement(name = "bearer-key") })
	@GetMapping(LAST_UPLOAD_INFO_URL)
	public ResponseEntity<LastUploadInfo> getLastUploadInfo() {
		Optional<LastUploadInfo> lastUploadInfo = lastUploadInfoService.getForCurrentUser();
		if (lastUploadInfo.isEmpty()) return ResponseEntity.notFound().build();
		return ResponseEntity.ok(lastUploadInfo.get());
	}
	
	@Operation(security = { @SecurityRequirement(name = "bearer-key") })
	@GetMapping(LAST_UPLOAD_INFO_UPDATE_URL)
	public ResponseEntity<LastUploadInfo> updateLastUploadInfo() {		
		Optional<LastUploadInfo> optLastUploadInfo = lastUploadInfoService.updateForCurrentUser();
		if (optLastUploadInfo.isEmpty()) return ResponseEntity.notFound().build();
		return ResponseEntity.ok(optLastUploadInfo.get());
	}
	
}