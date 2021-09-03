package svobodavlad.imagesprocessing.parameters;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import svobodavlad.imagesprocessing.security.User;
import svobodavlad.imagesprocessing.security.UserRepository;

@RestController
@RequiredArgsConstructor
public class ProcessingParametersUserController {

	private static final String PARAMETERS_USER_URL = "/parameters";
	private final ProcessingParametersUserRepository parametersRepository;
	private final UserRepository userRepository;

	@Operation(security = { @SecurityRequirement(name = "bearer-key") })
	@GetMapping(PARAMETERS_USER_URL)
	public ResponseEntity<ProcessingParametersUser> getProcessingParametersUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null)
			return ResponseEntity.notFound().build();
		Optional<User> optUser = userRepository.findByUsername(authentication.getName());
		Optional<ProcessingParametersUser> optParameters = parametersRepository.findByUser(optUser.get());
		if (optParameters.isEmpty()) return ResponseEntity.notFound().build();
		return ResponseEntity.ok(optParameters.get());
	}

}