package svobodavlad.imagesprocessing.testutil;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import svobodavlad.imagesprocessing.jpaentities.User;
import svobodavlad.imagesprocessing.security.UserRepository;
import svobodavlad.imagesprocessing.security.UserService;

@Component
public class SecurityTestUtil {

	@Autowired
	private UserService userService;
	
	@Autowired
	private UserRepository userRepository;	

	public static final String DEFAULT_USERNAME = "user";

	public User saveDefaultUser() {
		Optional<User> optUser = userRepository.findByUsername(DEFAULT_USERNAME);
		if (optUser.isPresent()) return optUser.get();
		return userService.registerUser(new User(DEFAULT_USERNAME, DEFAULT_USERNAME, DEFAULT_USERNAME));
	}
}