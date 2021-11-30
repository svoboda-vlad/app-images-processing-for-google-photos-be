package svobodavlad.imagesprocessing.security;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import svobodavlad.imagesprocessing.jpaentities.User;
import svobodavlad.imagesprocessing.testutil.SecurityMockUtil;
import svobodavlad.imagesprocessing.testutil.UnitTestTemplate;

class UserDetailsServiceImplTest extends UnitTestTemplate {

	@Autowired
	private UserDetailsService userDetailsService;

	@MockBean
	private UserRepository userRepository;

	@Test
	void testLoadUserByUsernameOk() {
		User mockedUser = SecurityMockUtil.getMockedDefaultUserInternal();
		this.given(userRepository.findByUsername(mockedUser.getUsername())).willReturn(Optional.of(mockedUser));

		this.assertThat(userDetailsService.loadUserByUsername(mockedUser.getUsername())).isEqualTo(mockedUser);
	}

	@Test
	void testLoadUserByUsernameThrowsException() {
		User mockedUser = SecurityMockUtil.getMockedDefaultUserInternal();
		this.given(userRepository.findByUsername(mockedUser.getUsername())).willReturn(Optional.empty());

		this.assertThatExceptionOfType(UsernameNotFoundException.class).isThrownBy(() -> {
			userDetailsService.loadUserByUsername(mockedUser.getUsername());
		});
	}

}
