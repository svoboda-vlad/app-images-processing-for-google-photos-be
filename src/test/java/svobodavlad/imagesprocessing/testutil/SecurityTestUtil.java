package svobodavlad.imagesprocessing.testutil;

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
		var optUser = userRepository.findByUsername(DEFAULT_USERNAME);
		if (optUser.isPresent()) return optUser.get();
		return userService.registerUser(new User().setUsername(DEFAULT_USERNAME).setGivenName(DEFAULT_USERNAME).setFamilyName(DEFAULT_USERNAME));
	}
}