package svobodavlad.imagesprocessing;

import javax.persistence.EntityExistsException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import svobodavlad.imagesprocessing.security.User;
import svobodavlad.imagesprocessing.security.UserRegister;
import svobodavlad.imagesprocessing.security.UserService;

@Component
public class StartupCommandLineRunner implements CommandLineRunner {

	private final Logger log = LoggerFactory.getLogger(StartupCommandLineRunner.class);

	@Autowired
	private AdminUserBean adminUser;

	@Autowired
	private UserService userService;

	@Autowired
	private PasswordEncoder encoder;

	@Override
	public void run(String... args) throws Exception {
		saveAdminUser();
	}

	private void saveAdminUser() {
		if (adminUser.getUsername() != null && adminUser.getPassword() != null) {
			UserRegister userRegister = new UserRegister(adminUser.getUsername(), adminUser.getPassword(),
					"Administator", "Administrator");
			User user = userRegister.toUserInternal(encoder);
			try {
				userService.registerAdminUser(user);
			} catch (EntityExistsException e) {
				log.info("Username {} already exists.", user.getUsername());
			}
		}
	}
}