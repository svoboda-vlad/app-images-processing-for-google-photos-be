package svobodavlad.imagesprocessing.testutil;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public abstract class MockMvcUtilWithSecurity extends MockMvcUtil {
	
	private static final String ADMIN_USERNAME = "admin";
	
	@Autowired
	private MockMvc mockMvc;

	public ResultActions mockMvcPerformGetAuthorizationAdminUser(String requestUrl) throws Exception {
		return mockMvc.perform(get(requestUrl)
				.with(jwt().jwt(jwt -> jwt.subject(ADMIN_USERNAME)).authorities(new SimpleGrantedAuthority(ADMIN_USERNAME)))
				.accept(MediaType.APPLICATION_JSON)).andDo(print());
	}

	public ResultActions mockMvcPerformGetAuthorizationDefaultUser(String requestUrl) throws Exception {
		return mockMvc.perform(get(requestUrl)
				.with(jwt()).accept(MediaType.APPLICATION_JSON)).andDo(print());
	}
	
	public ResultActions mockMvcPerformPutAuthorizationAdminUser(String requestUrl, String requestJson)
			throws Exception {
		return mockMvc.perform(put(requestUrl)
				.with(jwt().jwt(jwt -> jwt.subject(ADMIN_USERNAME)).authorities(new SimpleGrantedAuthority(ADMIN_USERNAME)))
				.content(requestJson).contentType(MediaType.APPLICATION_JSON)).andDo(print());
	}

	public ResultActions mockMvcPerformPutAuthorizationDefaultUser(String requestUrl, String requestJson)
			throws Exception {
		return mockMvc.perform(put(requestUrl)
				.with(jwt()).content(requestJson).contentType(MediaType.APPLICATION_JSON)).andDo(print());
	}

	public ResultActions mockMvcPerformDeleteAuthorizationDefaultUser(String requestUrl) throws Exception {
		return mockMvc.perform(delete(requestUrl).with(jwt()));
	}
}
