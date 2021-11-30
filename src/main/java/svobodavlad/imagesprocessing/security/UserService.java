package svobodavlad.imagesprocessing.security;

import java.util.Optional;

import javax.persistence.EntityExistsException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import svobodavlad.imagesprocessing.jpaentities.Role;
import svobodavlad.imagesprocessing.jpaentities.User;
import svobodavlad.imagesprocessing.parameters.ProcessingParametersUserService;

@Service
@Transactional
public class UserService {

	private final Logger log = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ProcessingParametersUserService parametersService;

	private static final String USER_ROLE_NAME = "ROLE_USER";
	private static final String ADMIN_ROLE_NAME = "ROLE_ADMIN";

	public User registerUser(User user) {

		Optional<Role> optRole = roleRepository.findByName(USER_ROLE_NAME);

		if (optRole.isEmpty()) {
			log.info("Role {} not found in database.", USER_ROLE_NAME);
			throw new RuntimeException("Role not found.");
		} else {
			if (userRepository.findByUsername(user.getUsername()).isPresent())
				throw new EntityExistsException("User already exists.");
			user.addRole(optRole.get());
			user = userRepository.save(user);
			parametersService.setInitialParameters(user.getUsername());
			return user;
		}
	}

	public User registerAdminUser(User user) {
		user = registerUser(user);

		Optional<Role> optRole = roleRepository.findByName(ADMIN_ROLE_NAME);

		if (optRole.isEmpty()) {
			log.info("Role {} not found in database.", ADMIN_ROLE_NAME);
			throw new RuntimeException("Role not found.");
		} else {
			user.addRole(optRole.get());
			return userRepository.save(user);
		}
	}

	public User updateLastLoginDateTime(String username) {
		Optional<User> optUser = userRepository.findByUsername(username);
		if (optUser.isPresent()) {
			User user = optUser.get();
			user.updateLastLoginDateTime();
			return userRepository.save(user);
		}
		return null;
	}

	public User updateUser(UserInfo userInfo) {
		Optional<User> optUser = userRepository.findByUsername(userInfo.getUsername());
		User user = optUser.get();
		return userRepository.save(userInfo.toUser(user));
	}
	
	public void deleteUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Optional<User> optUser = userRepository.findByUsername(authentication.getName());
		if (optUser.isPresent()) {
			parametersService.deleteForCurrentUser();
			userRepository.delete(optUser.get());
			userRepository.flush();
		}
	}

}