package svobodavlad.imagesprocessing.integration;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import svobodavlad.imagesprocessing.jpaentities.LastUploadInfo;
import svobodavlad.imagesprocessing.lastupload.LastUploadInfoRepository;
import svobodavlad.imagesprocessing.testutil.IntegTestTemplate;
import svobodavlad.imagesprocessing.testutil.SecurityTestUtil;

public class LastUploadInfoControllerIT extends IntegTestTemplate {
		
	@Autowired
	private LastUploadInfoRepository lastUploadInfoRepository;
	
	@Autowired
	private SecurityTestUtil securityTestUtil;
			
	@BeforeEach
	void initData() {
		lastUploadInfoRepository.save(new LastUploadInfo().setLastUploadDateTime(Instant.now()).setUser(securityTestUtil.saveDefaultUser()));
	}

	@Test
	void getLastUploadInfoOk200() throws Exception {		
		String requestUrl = "/last-upload-info";
		int expectedStatus = 200;
		DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.systemDefault());
		
		LastUploadInfo lastUploadInfo = lastUploadInfoRepository.findByUser_Username(SecurityTestUtil.DEFAULT_USERNAME).get();
		String expectedJson = "{\"lastUploadDateTime\":\"" + formatter.format(lastUploadInfo.getLastUploadDateTime()) + "\"}";
		
		ResultActions mvcResult = this.mockMvcPerformGetAuthorizationDefaultUser(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}
	
	@Test
	void getLastUploadInfoNotFound404() throws Exception {
		String requestUrl = "/last-upload-info";
		int expectedStatus = 404;
		String expectedJson = "";
		
		lastUploadInfoRepository.deleteAll();

		ResultActions mvcResult = this.mockMvcPerformGetAuthorizationDefaultUser(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);	
	}	

	@Test
	void updateLastUploadInfoOk200AndDateUpdatedWhenInfoExists() throws Exception {
		String requestUrl = "/last-upload-info-update";
		int expectedStatus = 200;

		Instant minTime = Instant.now();
		ResultActions mvcResult = this.mockMvcPerformGetAuthorizationDefaultUser(requestUrl);
		
		LastUploadInfo lastUploadInfo = lastUploadInfoRepository.findByUser_Username(SecurityTestUtil.DEFAULT_USERNAME).get();
		this.assertThat(lastUploadInfo.getLastUploadDateTime()).isBetween(minTime, Instant.now());
		
		DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.systemDefault());
		String expectedJson = "{\"lastUploadDateTime\":\"" + formatter.format(lastUploadInfo.getLastUploadDateTime()) + "\"}";		
		
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}
	
	@Test
	void updateLastUploadInfoOk200AndDateUpdatedWhenInfoDoesNotExists() throws Exception {
		String requestUrl = "/last-upload-info-update";
		int expectedStatus = 200;
		
		lastUploadInfoRepository.deleteAll();
		ResultActions mvcResult = this.mockMvcPerformGetAuthorizationDefaultUser(requestUrl);
		
		LastUploadInfo newLastUploadInfo = lastUploadInfoRepository.findByUser_Username(SecurityTestUtil.DEFAULT_USERNAME).get();
		DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.systemDefault());
		String expectedJson = "{\"lastUploadDateTime\":\"" + formatter.format(newLastUploadInfo.getLastUploadDateTime()) + "\"}";		
		
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}	
	
	@Test
	void updateLastUploadInfoNotFound404() throws Exception {
		String requestUrl = "/last-upload-info-upload";
		int expectedStatus = 404;
		String expectedJson = "";
		
		lastUploadInfoRepository.deleteAll();

		ResultActions mvcResult = this.mockMvcPerformGetAuthorizationDefaultUser(requestUrl);
		this.mockMvcExpectStatusAndContent(mvcResult, expectedStatus, expectedJson);
	}
	
}