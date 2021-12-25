package svobodavlad.imagesprocessing.testutil;

import java.time.Instant;
import java.time.LocalDateTime;

import org.assertj.core.api.AbstractInstantAssert;
import org.assertj.core.api.AbstractLocalDateTimeAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ObjectAssert;
import org.assertj.core.api.ThrowableTypeAssert;
import org.mockito.BDDMockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class IntegTestTemplate extends MockMvcUtilWithSecurity {

	public <T> BDDMockito.BDDMyOngoingStubbing<T> given(T methodCall) {
		return BDDMockito.given(methodCall);
	}
	
	public <T> ObjectAssert<T> assertThat(T actual) {
		return Assertions.assertThat(actual);
	}	
	
	public AbstractLocalDateTimeAssert<?> assertThat(LocalDateTime actual) {
		return Assertions.assertThat(actual);
	}
	
	public AbstractInstantAssert<?> assertThat(Instant actual) {
		return Assertions.assertThat(actual);
	}	
	
	public <T extends Throwable> ThrowableTypeAssert<T> assertThatExceptionOfType(final Class<? extends T> exceptionType) {
		return Assertions.assertThatExceptionOfType(exceptionType);
	}	
}
