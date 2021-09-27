package svobodavlad.imagesprocessing.testutil;

import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
//@WithMockUser - not needed
public class UnitTestMockMvcTemplate {

	@Autowired
	protected MockMvc mvc;
	
	public <T> BDDMockito.BDDMyOngoingStubbing<T> given(T methodCall) {
		return BDDMockito.given(methodCall);
		
	}
	
}
