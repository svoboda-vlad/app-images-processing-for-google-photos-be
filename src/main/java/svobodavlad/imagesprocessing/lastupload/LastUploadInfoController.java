package svobodavlad.imagesprocessing.lastupload;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Transactional
public class LastUploadInfoController {

	private static final String LAST_UPLOAD_INFO_URL = "/last-upload-info";
	private static final String LAST_UPLOAD_INFO_UPDATE_URL = "/last-upload-info-update";
	private final LastUploadInfoService lastUploadInfoService;

	@Operation(security = { @SecurityRequirement(name = "bearer-key") })
	@GetMapping(LAST_UPLOAD_INFO_URL)
	public ResponseEntity<LastUploadInfoTemplate> getLastUploadInfoTemplate() {
		var lastUploadInfo = lastUploadInfoService.getForCurrentUser();
		if (lastUploadInfo.isEmpty()) return ResponseEntity.notFound().build();
		return ResponseEntity.ok(lastUploadInfo.get().toLastUploadInfoTemplate());
	}
	
	@Operation(security = { @SecurityRequirement(name = "bearer-key") })
	@GetMapping(LAST_UPLOAD_INFO_UPDATE_URL)
	public ResponseEntity<LastUploadInfoTemplate> updateLastUploadInfoTemplate() {		
		var optLastUploadInfo = lastUploadInfoService.updateForCurrentUser();
		if (optLastUploadInfo.isEmpty()) return ResponseEntity.notFound().build();
		return ResponseEntity.ok(optLastUploadInfo.get().toLastUploadInfoTemplate());
	}
	
}