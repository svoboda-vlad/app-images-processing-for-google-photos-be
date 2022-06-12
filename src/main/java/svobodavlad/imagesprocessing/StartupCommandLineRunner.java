package svobodavlad.imagesprocessing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import svobodavlad.imagesprocessing.jpaentities.ProcessingParametersDefault;
import svobodavlad.imagesprocessing.jpaentities.User;
import svobodavlad.imagesprocessing.parameters.ProcessingParametersDefaultRepository;
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
	private ProcessingParametersDefaultRepository parametersRepository;

	@Override
	public void run(String... args) throws Exception {
		saveProcessingParametersDefault();
		saveAdminUser();
	}

	void saveAdminUser() {
		if (adminUser.getUsername() != null) {
			User user = new User(adminUser.getUsername(), "N/A", "N/A");
			userService.registerAdminUser(user);
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