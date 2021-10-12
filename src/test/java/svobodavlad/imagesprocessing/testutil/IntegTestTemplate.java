package svobodavlad.imagesprocessing.testutil;

import java.time.LocalDateTime;

import org.assertj.core.api.AbstractLocalDateTimeAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ObjectAssert;
import org.assertj.core.api.ThrowableTypeAssert;
import org.mockito.BDDMockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("liquibase")
public class IntegTestTemplate extends MockMvcUtil {
	
	public <T> BDDMockito.BDDMyOngoingStubbing<T> given(T methodCall) {
		return BDDMockito.given(methodCall);
	}
	
	public <T> ObjectAssert<T> assertThat(T actual) {
		return Assertions.assertThat(actual);
	}	
	
	public AbstractLocalDateTimeAssert<?> assertThat(LocalDateTime actual) {
		return Assertions.assertThat(actual);
	}	
	
	public <T extends Throwable> ThrowableTypeAssert<T> assertThatExceptionOfType(final Class<? extends T> exceptionType) {
		return Assertions.assertThatExceptionOfType(exceptionType);
	}	
	
}
