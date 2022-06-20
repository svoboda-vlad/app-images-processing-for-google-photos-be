package svobodavlad.imagesprocessing.lastupload;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

import svobodavlad.imagesprocessing.jpaentities.LastUploadInfo;
import svobodavlad.imagesprocessing.jpaentities.User;
import svobodavlad.imagesprocessing.testutil.UnitTestTemplateMockMvc;

@WebMvcTest(LastUploadInfoController.class)
@AutoConfigureMockMvc(addFilters = false)
public class LastUploadInfoControllerTest extends UnitTestTemplateMockMvc {

	private static final String MOCKED_USER_NAME = "user";
	
	@MockBean
	private LastUploadInfoService lastUploadInfoService;

	@Test
	void getLastUploadInfoOk200() throws Exception {
		String requestUrl = "/last-upload-info";
		int expectedStatus = 200;
		
		Instant lastUploadDateTime = Instant.now();
		DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.systemDefault());		
		String expectedJson = "{\"lastUploadDateTime\":\"" + formatter.format(lastUploadDateTime) + "\"}";
		
		User mockedUser = new User().setUsername(MOCKED_USER_NAME).setGivenName(MOCKED_USER_NAME).setFamilyName(MOCKED_USER_NAME);
		LastUploadInfo lastUploadInfo = new LastUploadInfo().setLastUploadDateTime(lastUploadDateTime).setUser(mockedUser);
		
		this.given(lastUploadInfoService.getForCurrentUser()).willReturn(Optional.of(lastUploadInfo));
		
		ResultActions mvcResult = this.mockMvcPerformGetNoAuthorization(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);		
	}
	
	@Test
	void getLastUploadInfoNotFound404() throws Exception {
		String requestUrl = "/last-upload-info";
		int expectedStatus = 404;
		String expectedJson = "";
		
		this.given(lastUploadInfoService.getForCurrentUser()).willReturn(Optional.empty());
		
		ResultActions mvcResult = this.mockMvcPerformGetNoAuthorization(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}

	@Test
	void updateLastUploadInfoOk200() throws Exception {
		String requestUrl = "/last-upload-info-update";
		int expectedStatus = 200;
		Instant lastUploadDateTime = Instant.now();
		DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.systemDefault());		
		String expectedJson = "{\"lastUploadDateTime\":\"" + formatter.format(lastUploadDateTime) +"\"}";
		
		User mockedUser = new User().setUsername(MOCKED_USER_NAME).setGivenName(MOCKED_USER_NAME).setFamilyName(MOCKED_USER_NAME);
		LastUploadInfo lastUploadInfo = new LastUploadInfo().setLastUploadDateTime(lastUploadDateTime).setUser(mockedUser);
		
		this.given(lastUploadInfoService.updateForCurrentUser()).willReturn(Optional.of(lastUploadInfo));
		
		ResultActions mvcResult = this.mockMvcPerformGetNoAuthorization(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}	
}