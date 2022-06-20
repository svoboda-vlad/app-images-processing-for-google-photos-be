package svobodavlad.imagesprocessing.security;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityExistsException;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import svobodavlad.imagesprocessing.jpaentities.User;
import svobodavlad.imagesprocessing.parameters.ProcessingParametersUserService;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final ProcessingParametersUserService parametersService;
	
	public User registerUser(User user) {
		if (userRepository.findByUsername(user.getUsername()).isPresent())
			throw new EntityExistsException("User already exists.");
		user = userRepository.save(user);
		parametersService.setInitialParameters(user.getUsername());
		return user;
	}
	
	public Optional<User> updateCurrentUser(UserTemplate userInfo) {
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
			return updateCurrentUser(getUserInfoWithAttributes(authentication));
		} else {
			UserTemplate userInfo = getUserInfoWithAttributes(authentication);
			User user = new User().setUsername(userInfo.getUsername()).setGivenName(userInfo.getGivenName()).setFamilyName(userInfo.getFamilyName());
			user.setEmail(userInfo.getEmail());
			return Optional.of(registerUser(user));
		}
	}
	
	private UserTemplate getUserInfoWithAttributes(Authentication authentication) {
		UserTemplate userInfo = new UserTemplate();
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