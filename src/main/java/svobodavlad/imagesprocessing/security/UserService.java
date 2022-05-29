package svobodavlad.imagesprocessing.security;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
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

	public Optional<User> updateCurrentUserLastLoginDateTime() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Optional<User> optUser = userRepository.findByUsername(authentication.getName());
		if (optUser.isEmpty()) return Optional.empty();
		User user = optUser.get();
		user.updateLastLoginDateTime();
		return Optional.of(userRepository.save(user));
	}
	
	public Optional<User> updateCurrentUser(UserInfo userInfo) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!authentication.getName().equals(userInfo.getUsername())) return Optional.empty();
		Optional<User> optUser = userRepository.findByUsername(userInfo.getUsername());
		if (optUser.isEmpty()) return Optional.empty();
		User user = optUser.get();
		return Optional.of(userRepository.save(userInfo.toUser(user)));
	}	
	
	public void deleteCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Optional<User> optUser = userRepository.findByUsername(authentication.getName());
		optUser.ifPresent(user -> {
			parametersService.deleteForCurrentUser();
			userRepository.delete(user);
			userRepository.flush();		
		});
	}
	
	public Optional<User> getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Optional<User> optUser = userRepository.findByUsername(authentication.getName());
		if (optUser.isPresent()) {
			updateCurrentUser(getUserInfoWithAttributes(authentication));
			return updateCurrentUserLastLoginDateTime();
		} else {
			UserInfo userInfo = getUserInfoWithAttributes(authentication);
			UserRegister userRegister = new UserRegister(
					userInfo.getUsername(), 
					userInfo.getGivenName(),
					userInfo.getFamilyName(), 
					userInfo.getEmail()
					);
			registerUser(userRegister.toUser());
			return updateCurrentUserLastLoginDateTime();
		}
	}
	
	private UserInfo getUserInfoWithAttributes(Authentication authentication) {
		UserInfo userInfo = new UserInfo();
		userInfo.setUsername(authentication.getName());
		userInfo.setGivenName(authentication.getName());
		userInfo.setFamilyName(authentication.getName());
		userInfo.setEmail(authentication.getName());
		if (authentication instanceof AbstractAuthenticationToken) {
			AbstractAuthenticationToken authToken = (AbstractAuthenticationToken) authentication;
			Map<String, Object> attributes = new HashMap<>();
			if (authToken instanceof JwtAuthenticationToken) {
	            attributes = ((JwtAuthenticationToken) authToken).getTokenAttributes();
	        }
			if (attributes.get("given_name") != null) userInfo.setGivenName((String) attributes.get("given_name"));
			if (attributes.get("family_name") != null) userInfo.setFamilyName((String) attributes.get("family_name"));
			if (attributes.get("email") != null) userInfo.setEmail((String) attributes.get("email"));
		}
		return userInfo;
	}
}