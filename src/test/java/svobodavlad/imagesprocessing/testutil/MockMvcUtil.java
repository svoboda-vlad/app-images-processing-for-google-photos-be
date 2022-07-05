package svobodavlad.imagesprocessing.testutil;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@AutoConfigureJsonTesters
public abstract class MockMvcUtil {

	protected static final int HTTP_OK = 200;
	protected static final int HTTP_NO_CONTENT = 204;
	protected static final int HTTP_UNAUTHORIZED = 401;
	protected static final int HTTP_NOT_FOUND = 404;
	
	@Autowired
	private MockMvc mockMvc;

	public ResultActions mockMvcPerformGetNoAuthorization(String requestUrl) throws Exception {
		return mockMvc.perform(get(requestUrl).accept(MediaType.APPLICATION_JSON));
	}

	public ResultActions mockMvcPerformPostNoAuthorization(String requestUrl, String requestJson) throws Exception {
		return mockMvc.perform(post(requestUrl).content(requestJson).contentType(MediaType.APPLICATION_JSON));
	}
	
	public ResultActions mockMvcPerformPutNoAuthorization(String requestUrl, String requestJson) throws Exception {
		return mockMvc.perform(put(requestUrl).content(requestJson).contentType(MediaType.APPLICATION_JSON));
	}	
	
	public ResultActions mockMvcPerformDeleteNoAuthorization(String requestUrl) throws Exception {
		return mockMvc.perform(delete(requestUrl));
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
}
