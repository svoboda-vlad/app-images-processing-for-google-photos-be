package svobodavlad.imagesprocessing.parameters;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import svobodavlad.imagesprocessing.security.User;
import svobodavlad.imagesprocessing.security.UserRepository;

@RestController
@RequiredArgsConstructor
@Transactional
public class ProcessingParametersUserController {

	private static final String PARAMETERS_USER_URL = "/parameters";
	private final ProcessingParametersUserRepository parametersRepository;
	private final UserRepository userRepository;

	@Operation(security = { @SecurityRequirement(name = "bearer-key") })
	@GetMapping(PARAMETERS_USER_URL)
	public ResponseEntity<ProcessingParametersUserTemplate> getProcessingParametersUserTemplate() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Optional<User> optUser = userRepository.findByUsername(authentication.getName());
		Optional<ProcessingParametersUser> optParameters = parametersRepository.findByUser(optUser.get());
		if (optParameters.isEmpty()) return ResponseEntity.notFound().build();
		return ResponseEntity.ok(optParameters.get().toProcessingParametersUserTemplate());
	}
	
	@Operation(security = { @SecurityRequirement(name = "bearer-key") })
	@PutMapping(PARAMETERS_USER_URL)
    // @PutMapping(PARAMETERS_USER_URL + "/{id}")
    // public ResponseEntity<ProcessingParametersUserTemplate> updateProcessingParametersUserTemplate(@Valid @RequestBody ProcessingParametersUserTemplate parameters, @PathVariable long id) throws URISyntaxException { 
	public ResponseEntity<ProcessingParametersUserTemplate> updateProcessingParametersUserTemplate(@Valid @RequestBody ProcessingParametersUserTemplate parametersTemplate) {		
        // if (parameters.getId() == 0L) return ResponseEntity.badRequest().build();
        // if (id != parameters.getId()) return ResponseEntity.badRequest().build();
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Optional<User> optUser = userRepository.findByUsername(authentication.getName());
		Optional<ProcessingParametersUser> optParameters = parametersRepository.findByUser(optUser.get());
		if (optParameters.isEmpty()) return ResponseEntity.notFound().build();
		ProcessingParametersUser parameters = parametersTemplate.toProcessingParametersUser(optParameters.get());
		parameters = parametersRepository.save(parameters);
		return ResponseEntity.ok(parameters.toProcessingParametersUserTemplate());
	}

}