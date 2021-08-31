package svobodavlad.imagesprocessing.parametersdefault;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ProcessingParametersDefaultController {

	private static final String PARAMETERS_DEFAULT_URL = "/admin/parameters-default";
	private final ProcessingParametersDefaultRepository parametersRepository;

	@Operation(security = { @SecurityRequirement(name = "bearer-key") })
	@GetMapping(PARAMETERS_DEFAULT_URL)
	public ResponseEntity<ProcessingParametersDefault> getProcessingParametersDefault() {
		List<ProcessingParametersDefault> parametersList = parametersRepository.findAll();
		if (parametersList.isEmpty()) return ResponseEntity.notFound().build();
		return ResponseEntity.ok(parametersList.get(0));
	}
	
	@Operation(security = { @SecurityRequirement(name = "bearer-key") })
	@PutMapping(PARAMETERS_DEFAULT_URL)
    // @PutMapping(PARAMETERS_DEFAULT_URL + "/{id}")
    // public ResponseEntity<ProcessingParametersDefault> updateProcessingParametersDefault(@Valid @RequestBody ProcessingParametersDefault parameters, @PathVariable long id) throws URISyntaxException { 
	public ResponseEntity<ProcessingParametersDefault> updateProcessingParametersDefault(@Valid @RequestBody ProcessingParametersDefault parameters) {		
        if (parameters.getId() == 0L) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        // if (id != parameters.getId()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        if (parametersRepository.findById(parameters.getId()).isEmpty()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        return ResponseEntity.ok(parametersRepository.save(parameters));
		
	}

}
