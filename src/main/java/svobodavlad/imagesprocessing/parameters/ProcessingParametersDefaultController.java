package svobodavlad.imagesprocessing.parameters;

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

@RestController
@RequiredArgsConstructor
@Transactional
public class ProcessingParametersDefaultController {

	private static final String PARAMETERS_DEFAULT_URL = "/admin/parameters-default";
	private final ProcessingParametersDefaultRepository parametersRepository;

	@Operation(security = { @SecurityRequirement(name = "bearer-key") })
	@GetMapping(PARAMETERS_DEFAULT_URL)
	public ResponseEntity<ProcessingParametersDefaultTemplate> getProcessingParametersDefaultTemplate() {
		var parametersList = parametersRepository.findAll();
		if (parametersList.isEmpty()) return ResponseEntity.notFound().build();
		return ResponseEntity.ok(parametersList.get(0).toProcessingParametersDefaultTemplate());
	}
	
	@Operation(security = { @SecurityRequirement(name = "bearer-key") })
	@PutMapping(PARAMETERS_DEFAULT_URL)
	public ResponseEntity<ProcessingParametersDefaultTemplate> updateProcessingParametersDefaultTemplate(@Valid @RequestBody ProcessingParametersDefaultTemplate parametersTemplate) {
		var parametersList = parametersRepository.findAll();
		if (parametersList.isEmpty()) return ResponseEntity.notFound().build();
		var parameters = parametersTemplate.toProcessingParametersDefault(parametersList.get(0));
		parameters = parametersRepository.save(parameters);
		return ResponseEntity.ok(parameters.toProcessingParametersDefaultTemplate());		
	}

}
