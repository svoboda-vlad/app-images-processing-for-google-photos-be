package svobodavlad.imagesprocessing.security;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import svobodavlad.imagesprocessing.testutil.SecurityMockUtil;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
//@WithMockUser - not needed
class LoginFilterTest {

	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private PasswordEncoder encoder;

	@MockBean
	private UserRepository userRepository;
	
	@MockBean
	private UserService userService;
	
	@MockBean
	private UserDetailsService userDetailsService;
	
	private User mockedUser;
	
	@BeforeEach
	private void initData() {
		mockedUser = SecurityMockUtil.getMockedDefaultUser();
		mockedUser.setPassword(encoder.encode("pass123"));
		given(userDetailsService.loadUserByUsername(mockedUser.getUsername())).willReturn(mockedUser);
		given(userRepository.findByUsername(mockedUser.getUsername())).willReturn(Optional.of(mockedUser));
	}	

	@Test
	void testLoginNoLastLoginDateTimeOk200() throws Exception {
		String requestUrl = "/login";
		String requestJson = "{\"username\":\"user1\",\"password\":\"pass123\"}";
		int expectedStatus = 200;
		String expectedJson = "";
		
		this.mvc.perform(post(requestUrl).content(requestJson).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is(expectedStatus)).andExpect(content().string(expectedJson))
				.andExpect(header().exists("Authorization"));
		
		verify(userService, times(1)).updateLastLoginDateTime(mockedUser.getUsername());
	}

}
