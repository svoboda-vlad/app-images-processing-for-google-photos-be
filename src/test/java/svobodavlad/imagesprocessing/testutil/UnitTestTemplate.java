package svobodavlad.imagesprocessing.testutil;

import java.time.LocalDateTime;

import org.assertj.core.api.AbstractLocalDateTimeAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ObjectAssert;
import org.assertj.core.api.ThrowableTypeAssert;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.mockito.verification.VerificationMode;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles(value = {"dev", "noliquibase"})
public class UnitTestTemplate extends MockMvcUtil {

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
