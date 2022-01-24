package svobodavlad.imagesprocessing.parameters;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import svobodavlad.imagesprocessing.jpaentities.ProcessingParametersUser;

@RestController
@RequiredArgsConstructor
@Transactional
public class ProcessingParametersUserController {

	private static final String PARAMETERS_USER_URL = "/parameters";
	private static final String PARAMETERS_USER_RESET_URL = "/parameters-reset-to-default";
	private final ProcessingParametersDefaultRepository parametersDefaultRepository;
	private final ProcessingParametersUserService parametersService;

	@Operation(security = { @SecurityRequirement(name = "bearer-key") })
	@GetMapping(PARAMETERS_USER_URL)
	public ResponseEntity<ProcessingParametersUserTemplate> getProcessingParametersUserTemplate() {
		Optional<ProcessingParametersUser> parameters = parametersService.getForCurrentUser();
		if (parameters.isEmpty()) return ResponseEntity.notFound().build();
		return ResponseEntity.ok(parameters.get().toProcessingParametersUserTemplate());
	}
	
	@Operation(security = { @SecurityRequirement(name = "bearer-key") })
	@PutMapping(PARAMETERS_USER_URL)
	public ResponseEntity<ProcessingParametersUserTemplate> updateProcessingParametersUserTemplate(@Valid @RequestBody ProcessingParametersUserTemplate parametersTemplate) {		
		Optional<ProcessingParametersUser> optParameters = parametersService.updateForCurrentUser(parametersTemplate);
		if (optParameters.isEmpty()) return ResponseEntity.notFound().build();
		return ResponseEntity.ok(optParameters.get().toProcessingParametersUserTemplate());
	}

	@Operation(security = { @SecurityRequirement(name = "bearer-key") })
	@GetMapping(PARAMETERS_USER_RESET_URL)
	public ResponseEntity<String> getResetToDefault() {
		if (parametersDefaultRepository.findAll().isEmpty()) return ResponseEntity.notFound().build();
		parametersService.resetToDefault();
		return ResponseEntity.ok(null);
	}
	
}