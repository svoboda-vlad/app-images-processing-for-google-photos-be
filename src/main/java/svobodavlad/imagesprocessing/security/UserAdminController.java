package svobodavlad.imagesprocessing.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import svobodavlad.imagesprocessing.jpaentities.User;

@RestController
@RequiredArgsConstructor
@Transactional
public class UserAdminController {

	private static final String ADMIN_USERS_URL = "/admin/users";
	private final UserRepository userRepository;

	@Operation(security = { @SecurityRequirement(name = "bearer-key") })
	@GetMapping(ADMIN_USERS_URL)
	public ResponseEntity<List<UserTemplate>> getAllUsers() {
		List<User> users = userRepository.findAll();
		List<UserTemplate> userTemplateList = new ArrayList<UserTemplate>();
		users.forEach(user -> {
			userTemplateList.add(user.toUserTemplate());
		});
		return ResponseEntity.ok(userTemplateList);
	}
	
}