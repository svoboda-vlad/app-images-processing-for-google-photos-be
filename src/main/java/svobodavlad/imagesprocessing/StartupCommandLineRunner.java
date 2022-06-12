package svobodavlad.imagesprocessing;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import svobodavlad.imagesprocessing.jpaentities.ProcessingParametersDefault;
import svobodavlad.imagesprocessing.jpaentities.User;
import svobodavlad.imagesprocessing.parameters.ProcessingParametersDefaultRepository;
import svobodavlad.imagesprocessing.security.UserRepository;
import svobodavlad.imagesprocessing.security.UserService;

@Component
public class StartupCommandLineRunner implements CommandLineRunner {

	private final static int TIME_DIFF_GROUP_DEFAULT = 1800;
	private final static int RESIZE_WIDTH_DEFAULT = 1000;
	private final static int RESIZE_HEIGHT_DEFAULT = 1000;

	@Autowired
	private AdminUserBean adminUser;

	@Autowired
	private UserService userService;
	
	@Autowired
	private UserRepository userRepository;	

	@Autowired
	private ProcessingParametersDefaultRepository parametersRepository;

	@Override
	public void run(String... args) throws Exception {
		saveProcessingParametersDefault();
		saveAdminUser();
	}

	void saveAdminUser() {
		String username = adminUser.getUsername();
		if (username != null) {
			Optional<User> optUser = userRepository.findByUsername(username);
			if (optUser.isEmpty()) {
				User user = new User(username, "N/A", "N/A");
				userService.registerAdminUser(user);
			} else {
				User user = optUser.get();
				if (!userService.isAdmin(user)) {
					user = userService.addAdminRole(optUser.get());
				}
				userRepository.save(user);
			}
		}
	}

	void saveProcessingParametersDefault() {
		if (parametersRepository.findAll().isEmpty()) {
			ProcessingParametersDefault parameters = new ProcessingParametersDefault(TIME_DIFF_GROUP_DEFAULT,
					RESIZE_WIDTH_DEFAULT, RESIZE_HEIGHT_DEFAULT);
			parametersRepository.save(parameters);
		}
	}
}