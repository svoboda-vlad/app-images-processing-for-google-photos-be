package svobodavlad.imagesprocessing.testutil;

import java.time.Instant;
import java.time.LocalDateTime;

import org.assertj.core.api.AbstractInstantAssert;
import org.assertj.core.api.AbstractLocalDateTimeAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ObjectAssert;
import org.assertj.core.api.ThrowableTypeAssert;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;
import org.mockito.verification.VerificationMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles(value = {"dev", "noliquibase"})
public class UnitTestTemplate {

	public <T> OngoingStubbing<T> when(T methodCall) {
		return Mockito.when(methodCall);
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
	
	public AbstractInstantAssert<?> assertThat(Instant actual) {
		return Assertions.assertThat(actual);
	}
	
	public <T extends Throwable> ThrowableTypeAssert<T> assertThatExceptionOfType(final Class<? extends T> exceptionType) {
		return Assertions.assertThatExceptionOfType(exceptionType);
	}
	
	public <T> T any(Class<T> type) {
		return ArgumentMatchers.any(type);
	}

}
