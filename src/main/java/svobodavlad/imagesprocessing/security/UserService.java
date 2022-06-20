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
	
	public Optional<User> updateCurrentUser(UserTemplate userTemplate) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!authentication.getName().equals(userTemplate.getUsername())) return Optional.empty();
		Optional<User> optUser = userRepository.findByUsername(userTemplate.getUsername());
		if (optUser.isEmpty()) return Optional.empty();
		User user = optUser.get();
		return Optional.of(userRepository.save(userTemplate.toUser(user)));
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
			return updateCurrentUser(getUserTemplateWithAttributes(authentication));
		} else {
			UserTemplate userTemplate = getUserTemplateWithAttributes(authentication);
			User user = new User().setUsername(userTemplate.getUsername()).setGivenName(userTemplate.getGivenName()).setFamilyName(userTemplate.getFamilyName());
			user.setEmail(userTemplate.getEmail());
			return Optional.of(registerUser(user));
		}
	}
	
	private UserTemplate getUserTemplateWithAttributes(Authentication authentication) {
		UserTemplate userTemplate = new UserTemplate();
		userTemplate.setUsername(authentication.getName());
		userTemplate.setGivenName(authentication.getName());
		userTemplate.setFamilyName(authentication.getName());
		userTemplate.setEmail(authentication.getName());
		if (authentication instanceof AbstractAuthenticationToken) {
			AbstractAuthenticationToken authToken = (AbstractAuthenticationToken) authentication;
			Map<String, Object> attributes = new HashMap<>();
			if (authToken instanceof JwtAuthenticationToken) {
	            attributes = ((JwtAuthenticationToken) authToken).getTokenAttributes();
	        }
			if (attributes.get("given_name") != null) userTemplate.setGivenName((String) attributes.get("given_name"));
			if (attributes.get("family_name") != null) userTemplate.setFamilyName((String) attributes.get("family_name"));
			if (attributes.get("email") != null) userTemplate.setEmail((String) attributes.get("email"));
		}
		return userTemplate;
	}
	
}