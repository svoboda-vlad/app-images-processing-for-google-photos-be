package svobodavlad.imagesprocessing.testutil;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.mockito.verification.VerificationMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
//@WithMockUser - not needed
public class UnitTestMockMvcTemplate {

	@Autowired
	protected MockMvc mockMvc;

	public <T> BDDMockito.BDDMyOngoingStubbing<T> given(T methodCall) {
		return BDDMockito.given(methodCall);
	}

	public <T> T verify(T mock, VerificationMode mode) {
		return Mockito.verify(mock, mode);
	}

	public VerificationMode times(int wantedNumberOfInvocations) {
		return Mockito.times(wantedNumberOfInvocations);
	}

	public VerificationMode never() {
		return Mockito.never();
	}

	public ResultActions mockMvcPerformPost(String requestUrl, String requestJson) throws Exception {
		return this.mockMvc.perform(post(requestUrl).content(requestJson).contentType(MediaType.APPLICATION_JSON));
	}

	public ResultActions mockMvcExpectStatusAndContent(ResultActions result, int expectedStatus, String expectedJson) throws Exception {
		if (expectedJson.isEmpty()) {
			return result.andExpect(status().is(expectedStatus)).andExpect(content().string(expectedJson));
		}
		return result.andExpect(status().is(expectedStatus)).andExpect(content().json(expectedJson));
	}

	public ResultActions mockMvcExpectHeaderExists(ResultActions result, String expectedHeader) throws Exception {
		return result.andExpect(header().exists(expectedHeader));
	}
	
	public ResultActions mockMvcExpectHeaderDoesNotExist(ResultActions result, String unexpectedHeader) throws Exception {
		return result.andExpect(header().doesNotExist(unexpectedHeader));
	}
}
