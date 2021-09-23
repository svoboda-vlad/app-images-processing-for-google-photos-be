package svobodavlad.imagesprocessing;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import javax.persistence.EntityExistsException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import svobodavlad.imagesprocessing.parameters.ProcessingParametersDefaultRepository;
import svobodavlad.imagesprocessing.security.User;
import svobodavlad.imagesprocessing.security.UserRegister;
import svobodavlad.imagesprocessing.security.UserService;

@SpringBootTest
class StartupCommandLineRunnerTest {

	@MockBean
	private AdminUserBean adminUser;

	@MockBean
	private UserService userService;

	@MockBean
	private PasswordEncoder encoder;

	@MockBean
	private ProcessingParametersDefaultRepository parametersRepository;
	
	@Autowired
	private StartupCommandLineRunner startupRunner;
	
	private static final String ADMIN_USERNAME = "admin";
	private static final String ADMIN_PASSWORD = "admin123";	
	private static final String ADMIN_GIVEN_NAME = "Administrator";
	private static final String ADMIN_FAMILY_NAME = "Administrator";
	
	@Test
	void testSaveAdminUserUserServiceRegisterAdminUserIsCalled() {
		String encodedPassword = "A".repeat(60);
		
		given(adminUser.getUsername()).willReturn(ADMIN_USERNAME);
		given(adminUser.getPassword()).willReturn(ADMIN_PASSWORD);
		given(encoder.encode(ADMIN_PASSWORD)).willReturn(encodedPassword);
		
		UserRegister adminUserRegister = new UserRegister(ADMIN_USERNAME, ADMIN_PASSWORD, ADMIN_GIVEN_NAME, ADMIN_FAMILY_NAME);
		User mockedAdminUser = adminUserRegister.toUserInternal(encoder);		
		
		given(userService.registerAdminUser(mockedAdminUser)).willReturn(mockedAdminUser);		
		
		startupRunner.saveAdminUser();
		verify(userService, times(1)).registerAdminUser(mockedAdminUser);
	}
	
	@Test
	void testSaveAdminUserThrowsException() {
		String encodedPassword = "A".repeat(60);
		
		given(adminUser.getUsername()).willReturn(ADMIN_USERNAME);
		given(adminUser.getPassword()).willReturn(ADMIN_PASSWORD);
		given(encoder.encode(ADMIN_PASSWORD)).willReturn(encodedPassword);
		
		UserRegister adminUserRegister = new UserRegister(ADMIN_USERNAME, ADMIN_PASSWORD, ADMIN_GIVEN_NAME, ADMIN_FAMILY_NAME);
		User mockedAdminUser = adminUserRegister.toUserInternal(encoder);		
		
		given(userService.registerAdminUser(mockedAdminUser)).willThrow(new EntityExistsException());		
		
		startupRunner.saveAdminUser();
		verify(userService, times(1)).registerAdminUser(mockedAdminUser);
	}
	
	@Test
	void testSaveAdminUserUsernameAndPasswordNull() {		
		given(adminUser.getUsername()).willReturn(null);
		given(adminUser.getPassword()).willReturn(null);		
		startupRunner.saveAdminUser();
		
		UserRegister adminUserRegister = new UserRegister(ADMIN_USERNAME, ADMIN_PASSWORD, ADMIN_GIVEN_NAME, ADMIN_FAMILY_NAME);
		User mockedAdminUser = adminUserRegister.toUserInternal(encoder);		
		verify(userService, times(0)).registerAdminUser(mockedAdminUser);
	}	

}
