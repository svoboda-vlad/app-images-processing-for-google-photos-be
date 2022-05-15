package svobodavlad.imagesprocessing;

import javax.persistence.EntityExistsException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import svobodavlad.imagesprocessing.jpaentities.ProcessingParametersDefault;
import svobodavlad.imagesprocessing.jpaentities.User;
import svobodavlad.imagesprocessing.parameters.ProcessingParametersDefaultRepository;
import svobodavlad.imagesprocessing.security.UserRegister;
import svobodavlad.imagesprocessing.security.UserService;

@Component
public class StartupCommandLineRunner implements CommandLineRunner {

	private final Logger log = LoggerFactory.getLogger(StartupCommandLineRunner.class);

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
		if (adminUser.getUsername() != null && adminUser.getPassword() != null) {
			UserRegister userRegister = new UserRegister(adminUser.getUsername(),
					"Administrator", "Administrator", null);
			User user = userRegister.toUser();
			try {
				userService.registerAdminUser(user);
			} catch (EntityExistsException e) {
				log.info("Username {} already exists.", user.getUsername());
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