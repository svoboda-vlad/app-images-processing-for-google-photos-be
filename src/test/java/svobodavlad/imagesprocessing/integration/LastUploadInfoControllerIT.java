package svobodavlad.imagesprocessing.integration;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.web.servlet.ResultActions;

import svobodavlad.imagesprocessing.jpaentities.LastUploadInfo;
import svobodavlad.imagesprocessing.lastupload.LastUploadInfoRepository;
import svobodavlad.imagesprocessing.lastupload.LastUploadInfoTemplate;
import svobodavlad.imagesprocessing.testutil.IntegTestTemplate;
import svobodavlad.imagesprocessing.testutil.SecurityTestUtil;

public class LastUploadInfoControllerIT extends IntegTestTemplate {

	private static final String LAST_UPLOAD_INFO_URL = "/last-upload-info";
	private static final String LAST_UPLOAD_INFO_UPDATE_URL = "/last-upload-info-update";
	
	private static final int HTTP_OK = 200;
	private static final int HTTP_NOT_FOUND = 404;
	
	@Autowired
	private LastUploadInfoRepository lastUploadInfoRepository;
	
	@Autowired
	private SecurityTestUtil securityTestUtil;
			
    @Autowired
    private JacksonTester<LastUploadInfoTemplate> jacksonTester;	
	
	@BeforeEach
	void initData() {
		lastUploadInfoRepository.save(new LastUploadInfo().setLastUploadDateTime(Instant.now()).setUser(securityTestUtil.saveDefaultUser()));
	}

	@Test
	void getLastUploadInfoOk200() throws Exception {
		var lastUploadInfo = lastUploadInfoRepository.findByUser_Username(SecurityTestUtil.DEFAULT_USERNAME).get();
		var expectedJson = jacksonTester.write(lastUploadInfo.toLastUploadInfoTemplate()).getJson();
		
		ResultActions mvcResult = this.mockMvcPerformGetAuthorizationDefaultUser(LAST_UPLOAD_INFO_URL);
		this.mockMvcExpectStatusAndContent(mvcResult, HTTP_OK, expectedJson);
	}
	
	@Test
	void getLastUploadInfoNotFound404() throws Exception {
		lastUploadInfoRepository.deleteAll();

		var mvcResult = this.mockMvcPerformGetAuthorizationDefaultUser(LAST_UPLOAD_INFO_URL);
		this.mockMvcExpectStatusAndContent(mvcResult, HTTP_NOT_FOUND, "");	
	}	

	@Test
	void updateLastUploadInfoOk200AndDateUpdatedWhenInfoExists() throws Exception {
		var minTime = Instant.now();
		var mvcResult = this.mockMvcPerformGetAuthorizationDefaultUser(LAST_UPLOAD_INFO_UPDATE_URL);
		
		var lastUploadInfo = lastUploadInfoRepository.findByUser_Username(SecurityTestUtil.DEFAULT_USERNAME).get();
		this.assertThat(lastUploadInfo.getLastUploadDateTime()).isBetween(minTime, Instant.now());
		var expectedJson = jacksonTester.write(lastUploadInfo.toLastUploadInfoTemplate()).getJson();		
		
		this.mockMvcExpectStatusAndContent(mvcResult, HTTP_OK, expectedJson);
	}
	
	@Test
	void updateLastUploadInfoOk200AndDateUpdatedWhenInfoDoesNotExists() throws Exception {
		lastUploadInfoRepository.deleteAll();
		var mvcResult = this.mockMvcPerformGetAuthorizationDefaultUser(LAST_UPLOAD_INFO_UPDATE_URL);
		
		var newLastUploadInfo = lastUploadInfoRepository.findByUser_Username(SecurityTestUtil.DEFAULT_USERNAME).get();
		var expectedJson = jacksonTester.write(newLastUploadInfo.toLastUploadInfoTemplate()).getJson();		
		this.mockMvcExpectStatusAndContent(mvcResult, HTTP_OK, expectedJson);
	}
	
}