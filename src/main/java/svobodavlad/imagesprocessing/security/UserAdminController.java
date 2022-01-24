package svobodavlad.imagesprocessing.security;

import java.util.ArrayList;
import java.util.List;
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
import svobodavlad.imagesprocessing.jpaentities.User;

@RestController
@RequiredArgsConstructor
@Transactional
public class UserAdminController {

	private static final String ADMIN_USERS_URL = "/admin/users";
	private static final String ADMIN_USER_URL = "/admin/user";
	private final UserRepository userRepository;
	private final UserService userService;

	@Operation(security = { @SecurityRequirement(name = "bearer-key") })
	@GetMapping(ADMIN_USERS_URL)
	public ResponseEntity<List<UserInfo>> getAllUsers() {
		List<User> users = userRepository.findAll();
		List<UserInfo> userInfoList = new ArrayList<UserInfo>();
		users.forEach(user -> {
			userInfoList.add(user.toUserInfo());
		});
		return ResponseEntity.ok(userInfoList);
	}
	
	@Operation(security = { @SecurityRequirement(name = "bearer-key") })
	@PutMapping(ADMIN_USER_URL)
	public ResponseEntity<UserInfo> updateUser(@Valid @RequestBody UserInfo userInfo) {
		Optional<User> optUpdatedUser = userService.updateCurrentUser(userInfo);
		if (optUpdatedUser.isEmpty()) return ResponseEntity.badRequest().build();
		return ResponseEntity.ok(optUpdatedUser.get().toUserInfo());
	}
	
}