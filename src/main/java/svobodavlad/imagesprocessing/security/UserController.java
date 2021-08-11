package svobodavlad.imagesprocessing.security;

import java.util.Optional;

import javax.persistence.EntityExistsException;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {

	private static final String USER_URL = "/user";
	private final UserRepository userRepository;
	private final PasswordEncoder encoder;
	private final UserService userService;

	@PostMapping(USER_URL)
	public ResponseEntity<String> registerUser(@Valid @RequestBody UserRegister userRegister) {
		User user = userRegister.toUserInternal(encoder);
		try {
			userService.registerUser(user);
		} catch (EntityExistsException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@Operation(security = { @SecurityRequirement(name = "bearer-key") })
	@GetMapping(USER_URL)
	public ResponseEntity<UserInfo> getUserInfo() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null)
			return new ResponseEntity<UserInfo>(HttpStatus.NOT_FOUND);
		Optional<User> optUser = userRepository.findByUsername(authentication.getName());
		return ResponseEntity.ok(optUser.get().toUserInfo());
	}

	@Operation(security = { @SecurityRequirement(name = "bearer-key") })
	@PutMapping(USER_URL)
	public ResponseEntity<UserInfo> updateUser(@Valid @RequestBody UserInfo userInfo) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!authentication.getName().equals(userInfo.getUsername()))
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		return ResponseEntity.ok(userService.updateUser(userInfo).toUserInfo());
	}

	@Operation(security = { @SecurityRequirement(name = "bearer-key") })
	@DeleteMapping(USER_URL)
	public ResponseEntity<UserInfo> deleteUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Optional<User> optUser = userRepository.findByUsername(authentication.getName());
		userRepository.deleteById(optUser.get().getId());
		return ResponseEntity.noContent().build();
	}

}