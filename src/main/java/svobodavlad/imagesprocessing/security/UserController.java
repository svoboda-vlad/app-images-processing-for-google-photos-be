package svobodavlad.imagesprocessing.security;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Transactional
public class UserController {

	private static final String USER_URL = "/user";
	private final UserService userService;
	
	@Operation(security = { @SecurityRequirement(name = "bearer-key") })
	@GetMapping(USER_URL)
	public ResponseEntity<UserTemplate> getUserTemplate() {
		var currentUser = userService.getCurrentUser();
		if (currentUser.isEmpty()) return ResponseEntity.notFound().build();
		return ResponseEntity.ok(currentUser.get().toUserTemplate());
	}

	@Operation(security = { @SecurityRequirement(name = "bearer-key") })
	@DeleteMapping(USER_URL)
	public ResponseEntity<Void> deleteUser() {
		userService.deleteCurrentUser();
		return ResponseEntity.noContent().build();
	}

}