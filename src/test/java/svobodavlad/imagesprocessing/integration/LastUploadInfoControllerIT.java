package svobodavlad.imagesprocessing.integration;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import svobodavlad.imagesprocessing.jpaentities.LastUploadInfo;
import svobodavlad.imagesprocessing.jpaentities.User;
import svobodavlad.imagesprocessing.lastupload.LastUploadInfoRepository;
import svobodavlad.imagesprocessing.security.UserRegister;
import svobodavlad.imagesprocessing.testutil.IntegTestTemplate;

public class LastUploadInfoControllerIT extends IntegTestTemplate {
	
	private static final String MOCKED_USER_NAME = "user";
	
	@Autowired
	private LastUploadInfoRepository lastUploadInfoRepository;
	
	LastUploadInfo lastUploadInfo;
	User mockedUser;
			
	@BeforeEach
	void initData() {
		mockedUser = new UserRegister(MOCKED_USER_NAME, MOCKED_USER_NAME, MOCKED_USER_NAME, null).toUser();
		lastUploadInfo = lastUploadInfoRepository.save(new LastUploadInfo(Instant.now(), mockedUser));
	}

	@Test
	void testGetLastUploadInfoOk200() throws Exception {		
		String requestUrl = "/last-upload-info";
		int expectedStatus = 200;
		DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.systemDefault());
		String expectedJson = "{\"id\":" + lastUploadInfo.getId() 
		+ ",\"lastUploadDateTime\":\"" + formatter.format(lastUploadInfo.getLastUploadDateTime()) + "\"}";
		
		ResultActions mvcResult = this.mockMvcPerformGetAuthorizationDefaultUser(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}
	
	@Test
	void testGetLastUploadInfoNotFound404() throws Exception {
		String requestUrl = "/last-upload-info";
		int expectedStatus = 404;
		String expectedJson = "";
		
		lastUploadInfoRepository.deleteAll();

		ResultActions mvcResult = this.mockMvcPerformGetAuthorizationDefaultUser(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);	
	}	

	@Test
	void testUpdateLastUploadInfoOk200AndDateUpdatedWhenInfoExists() throws Exception {
		String requestUrl = "/last-upload-info-update";
		int expectedStatus = 200;

		Instant minTime = Instant.now();
		ResultActions mvcResult = this.mockMvcPerformGetAuthorizationDefaultUser(requestUrl);
		
		lastUploadInfo = lastUploadInfoRepository.findById(lastUploadInfo.getId()).get();
		this.assertThat(lastUploadInfo.getLastUploadDateTime()).isBetween(minTime, Instant.now());
		
		DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.systemDefault());
		String expectedJson = "{\"id\":" + lastUploadInfo.getId() 
		+ ",\"lastUploadDateTime\":\"" + formatter.format(lastUploadInfo.getLastUploadDateTime()) + "\"}";		
		
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}
	
	@Test
	void testUpdateLastUploadInfoOk200AndDateUpdatedWhenInfoDoesNotExists() throws Exception {
		String requestUrl = "/last-upload-info-update";
		int expectedStatus = 200;
		
		lastUploadInfoRepository.deleteAll();
		Instant minTime = Instant.now();
		ResultActions mvcResult = this.mockMvcPerformGetAuthorizationDefaultUser(requestUrl);
		
		lastUploadInfo = lastUploadInfoRepository.findByUser(mockedUser).get();
		this.assertThat(lastUploadInfo.getLastUploadDateTime()).isBetween(minTime, Instant.now());
		
		DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.systemDefault());
		String expectedJson = "{\"id\":" + lastUploadInfo.getId() 
		+ ",\"lastUploadDateTime\":\"" + formatter.format(lastUploadInfo.getLastUploadDateTime()) + "\"}";		
		
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}	
	
	@Test
	void testUpdateLastUploadInfoNotFound404() throws Exception {
		String requestUrl = "/last-upload-info-upload";
		int expectedStatus = 404;
		String expectedJson = "";
		
		lastUploadInfoRepository.deleteAll();

		ResultActions mvcResult = this.mockMvcPerformGetAuthorizationDefaultUser(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}
	
}