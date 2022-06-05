package svobodavlad.imagesprocessing.testutil;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@AutoConfigureMockMvc
public abstract class MockMvcUtilWithSecurity extends MockMvcUtil {

	@Autowired
	private MockMvc mockMvc;

	public ResultActions mockMvcPerformGetAuthorizationAdminUser(String requestUrl) throws Exception {
		return this.mockMvc.perform(get(requestUrl)
				.with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"), new SimpleGrantedAuthority("ROLE_ADMIN")))
				.accept(MediaType.APPLICATION_JSON));
	}

	public ResultActions mockMvcPerformGetAuthorizationDefaultUser(String requestUrl) throws Exception {
		return this.mockMvc.perform(get(requestUrl)
				.with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))
				.accept(MediaType.APPLICATION_JSON));
	}
	
	public ResultActions mockMvcPerformPostNoAuthorization(String requestUrl, String requestJson) throws Exception {
		return this.mockMvc.perform(post(requestUrl).content(requestJson).contentType(MediaType.APPLICATION_JSON));
	}

	public ResultActions mockMvcPerformPutAuthorizationAdminUser(String requestUrl, String requestJson)
			throws Exception {
		return this.mockMvc.perform(put(requestUrl)
				.with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"), new SimpleGrantedAuthority("ROLE_ADMIN")))
				.content(requestJson).contentType(MediaType.APPLICATION_JSON));
	}

	public ResultActions mockMvcPerformPutAuthorizationDefaultUser(String requestUrl, String requestJson)
			throws Exception {
		return this.mockMvc.perform(put(requestUrl)
				.with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))
				.content(requestJson).contentType(MediaType.APPLICATION_JSON));
	}

	public ResultActions mockMvcPerformDeleteAuthorizationDefaultUser(String requestUrl) throws Exception {
		return this.mockMvc.perform(delete(requestUrl)
				.with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"), new SimpleGrantedAuthority("ROLE_ADMIN"))));
	}
	
	public ResultActions mockMvcPerformDeleteNoAuthorization(String requestUrl) throws Exception {
		return this.mockMvc.perform(delete(requestUrl));
	}	

	public ResultActions mockMvcExpectStatusAndContent(ResultActions result, int expectedStatus, String expectedJson)
			throws Exception {
		if (expectedJson.isEmpty()) {
			return result.andExpect(status().is(expectedStatus)).andExpect(content().string(expectedJson));
		}
		return result.andExpect(status().is(expectedStatus)).andExpect(content().json(expectedJson, true));
	}

	public ResultActions mockMvcExpectHeaderExists(ResultActions result, String expectedHeader) throws Exception {
		return result.andExpect(header().exists(expectedHeader));
	}

	public ResultActions mockMvcExpectHeaderDoesNotExist(ResultActions result, String unexpectedHeader)
			throws Exception {
		return result.andExpect(header().doesNotExist(unexpectedHeader));
	}
	
	public ResultActions mockMvcPerformPutNoAuthorization(String requestUrl, String requestJson) throws Exception {
		return this.mockMvc.perform(put(requestUrl).content(requestJson).contentType(MediaType.APPLICATION_JSON));
	}	
}
