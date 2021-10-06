package svobodavlad.imagesprocessing.security;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import svobodavlad.imagesprocessing.security.User.LoginProvider;
import svobodavlad.imagesprocessing.testutil.UnitTestTemplate;

class UserDetailsServiceImplTest extends UnitTestTemplate {

	@Autowired
	private PasswordEncoder encoder;

	@Autowired
	private UserDetailsService userDetailsService;

	@MockBean
	private UserRepository userRepository;

	@Test
	void testLoadUserByUsernameOk() {
		User user = new User("user", encoder.encode("password"), LoginProvider.INTERNAL, "User", "User", null, null);

		this.given(userRepository.findByUsername(user.getUsername())).willReturn(Optional.of(user));

		this.assertThat(userDetailsService.loadUserByUsername(user.getUsername())).isEqualTo(user);
	}

	@Test
	void testLoadUserByUsernameThrowsException() {
		User user = new User("user", encoder.encode("password"), LoginProvider.INTERNAL, "User", "User", null, null);

		this.given(userRepository.findByUsername(user.getUsername())).willReturn(Optional.empty());

		this.assertThatExceptionOfType(UsernameNotFoundException.class).isThrownBy(() -> {
			userDetailsService.loadUserByUsername(user.getUsername());
		});
	}

}
