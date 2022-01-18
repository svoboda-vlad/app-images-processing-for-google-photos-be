package svobodavlad.imagesprocessing.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import svobodavlad.imagesprocessing.testutil.UnitTestTemplateWithSecurity;

class AuthenticationFilterTest extends UnitTestTemplateWithSecurity {

	@Autowired
	private AuthenticationFilter authenticationFilter;	
	
	@MockBean
	private AuthenticationService authenticationService;

	@Test
	void testDoFilter() throws Exception {
		MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		MockHttpServletResponse mockResponse = new MockHttpServletResponse();
		MockFilterChain mockFilterChain = new MockFilterChain();
		
		authenticationFilter.doFilter(mockRequest, mockResponse, mockFilterChain);
		this.verify(authenticationService, this.times(1)).getAuthentication(mockRequest);
	}	
	

}
