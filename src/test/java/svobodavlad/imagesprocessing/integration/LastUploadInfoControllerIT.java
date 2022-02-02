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
import svobodavlad.imagesprocessing.testutil.IntegTestTemplate;
import svobodavlad.imagesprocessing.testutil.SecurityTestUtil;

public class LastUploadInfoControllerIT extends IntegTestTemplate {
	
	@Autowired
	private SecurityTestUtil securityTestUtil;
	
	@Autowired
	private LastUploadInfoRepository lastUploadInfoRepository;
	
	LastUploadInfo lastUploadInfo;
	User user;
			
	@BeforeEach
	void initData() {
		user = securityTestUtil.saveDefaultUserInternal();
		lastUploadInfo = lastUploadInfoRepository.save(new LastUploadInfo(Instant.now(), user));
	}

	@Test
	void testGetLastUploadInfoOk200() throws Exception {		
		String requestUrl = "/last-upload-info";
		int expectedStatus = 200;
		DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.systemDefault());
		String expectedJson = "{\"id\":" + lastUploadInfo.getId() 
		+ ",\"lastUploadDateTime\":\"" + formatter.format(lastUploadInfo.getLastUploadDateTime()) + "\"}";
		
		ResultActions mvcResult = this.mockMvcPerformGetAuthorizationDefaultUserInternal(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}
	
	@Test
	void testGetLastUploadInfoNotFound404() throws Exception {
		String requestUrl = "/last-upload-info";
		int expectedStatus = 404;
		String expectedJson = "";
		
		lastUploadInfoRepository.deleteAll();

		ResultActions mvcResult = this.mockMvcPerformGetAuthorizationDefaultUserInternal(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);	
	}	

	@Test
	void testUpdateLastUploadInfoOk200AndDateUpdatedWhenInfoExists() throws Exception {
		String requestUrl = "/last-upload-info-update";
		int expectedStatus = 200;

		Instant minTime = Instant.now();
		ResultActions mvcResult = this.mockMvcPerformGetAuthorizationDefaultUserInternal(requestUrl);
		
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
		ResultActions mvcResult = this.mockMvcPerformGetAuthorizationDefaultUserInternal(requestUrl);
		
		lastUploadInfo = lastUploadInfoRepository.findByUser(user).get();
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

		ResultActions mvcResult = this.mockMvcPerformGetAuthorizationDefaultUserInternal(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}
	
}