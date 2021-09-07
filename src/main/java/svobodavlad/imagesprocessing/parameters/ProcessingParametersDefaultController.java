package svobodavlad.imagesprocessing.parameters;

import java.util.List;

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
		List<ProcessingParametersDefault> parametersList = parametersRepository.findAll();
		if (parametersList.isEmpty()) return ResponseEntity.notFound().build();
		return ResponseEntity.ok(parametersList.get(0).toProcessingParametersDefaultTemplate());
	}
	
	@Operation(security = { @SecurityRequirement(name = "bearer-key") })
	@PutMapping(PARAMETERS_DEFAULT_URL)
    // @PutMapping(PARAMETERS_DEFAULT_URL + "/{id}")
    // public ResponseEntity<ProcessingParametersDefaultTemplate> updateProcessingParametersDefaultTemplate(@Valid @RequestBody ProcessingParametersDefaultTemplate parameters, @PathVariable long id) throws URISyntaxException { 
	public ResponseEntity<ProcessingParametersDefaultTemplate> updateProcessingParametersDefaultTemplate(@Valid @RequestBody ProcessingParametersDefaultTemplate parametersTemplate) {		
        // if (parameters.getId() == 0L) return ResponseEntity.badRequest().build();
        // if (id != parameters.getId()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        // if (parametersRepository.findById(parameters.getId()).isEmpty()) return ResponseEntity.badRequest().build();
		List<ProcessingParametersDefault> optParameters = parametersRepository.findAll();
		if (optParameters.isEmpty()) return ResponseEntity.notFound().build();
		ProcessingParametersDefault parameters = parametersTemplate.toProcessingParametersDefault(optParameters.get(0));
		parameters = parametersRepository.save(parameters);
		return ResponseEntity.ok(parameters.toProcessingParametersDefaultTemplate());		
	}

}
