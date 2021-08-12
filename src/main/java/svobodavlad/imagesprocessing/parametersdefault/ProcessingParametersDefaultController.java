package svobodavlad.imagesprocessing.parametersdefault;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
		return ResponseEntity.ok(parametersList.get(0));
	}

}
