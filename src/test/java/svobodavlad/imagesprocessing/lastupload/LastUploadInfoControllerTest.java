package svobodavlad.imagesprocessing.lastupload;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;

import svobodavlad.imagesprocessing.jpaentities.LastUploadInfo;
import svobodavlad.imagesprocessing.jpaentities.User;
import svobodavlad.imagesprocessing.testutil.UnitTestTemplateMockMvc;

@WebMvcTest(LastUploadInfoController.class)
@AutoConfigureMockMvc(addFilters = false)
public class LastUploadInfoControllerTest extends UnitTestTemplateMockMvc {

	private static final String MOCKED_USER_NAME = "user";
	
	private static final String LAST_UPLOAD_INFO_URL = "/last-upload-info";
	private static final String LAST_UPLOAD_INFO_UPDATE_URL = "/last-upload-info-update";
	
	private static final int HTTP_OK = 200;
	private static final int HTTP_NOT_FOUND = 404;
	
	@MockBean
	private LastUploadInfoService lastUploadInfoService;
	
    @Autowired
    private JacksonTester<LastUploadInfoTemplate> jacksonTester;	

	@Test
	void getLastUploadInfoOk200() throws Exception {
		var lastUploadDateTime = Instant.now();
		var mockedUser = new User().setUsername(MOCKED_USER_NAME).setGivenName(MOCKED_USER_NAME).setFamilyName(MOCKED_USER_NAME);
		var lastUploadInfo = new LastUploadInfo().setLastUploadDateTime(lastUploadDateTime).setUser(mockedUser);
		given(lastUploadInfoService.getForCurrentUser()).willReturn(Optional.of(lastUploadInfo));
		var expectedJson = jacksonTester.write(lastUploadInfo.toLastUploadInfoTemplate()).getJson();
		
		var mvcResult = mockMvcPerformGetNoAuthorization(LAST_UPLOAD_INFO_URL);
		mockMvcExpectStatusAndContent(mvcResult, HTTP_OK, expectedJson);		
	}
	
	@Test
	void getLastUploadInfoNotFound404() throws Exception {
		given(lastUploadInfoService.getForCurrentUser()).willReturn(Optional.empty());
		
		var mvcResult = mockMvcPerformGetNoAuthorization(LAST_UPLOAD_INFO_URL);
		mockMvcExpectStatusAndContent(mvcResult, HTTP_NOT_FOUND, "");
	}

	@Test
	void updateLastUploadInfoOk200() throws Exception {
		var lastUploadDateTime = Instant.now();	
		var mockedUser = new User().setUsername(MOCKED_USER_NAME).setGivenName(MOCKED_USER_NAME).setFamilyName(MOCKED_USER_NAME);
		var lastUploadInfo = new LastUploadInfo().setLastUploadDateTime(lastUploadDateTime).setUser(mockedUser);
		given(lastUploadInfoService.updateForCurrentUser()).willReturn(Optional.of(lastUploadInfo));
		var expectedJson = jacksonTester.write(lastUploadInfo.toLastUploadInfoTemplate()).getJson();
		
		var mvcResult = mockMvcPerformGetNoAuthorization(LAST_UPLOAD_INFO_UPDATE_URL);
		mockMvcExpectStatusAndContent(mvcResult, HTTP_OK, expectedJson);
	}	
}