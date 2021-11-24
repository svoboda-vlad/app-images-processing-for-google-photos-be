package svobodavlad.imagesprocessing.testutil;

import java.time.LocalDateTime;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.assertj.core.api.AbstractLocalDateTimeAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ObjectAssert;
import org.assertj.core.api.ThrowableTypeAssert;
import org.junit.jupiter.api.AfterEach;
import org.mockito.BDDMockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class IntegTestTemplate extends MockMvcUtil {
	
	@PersistenceContext
	EntityManager entityManager;

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
	
	@AfterEach
	public void flushSession() {
	    // Manual flush is required to avoid false positive in test
	    entityManager.flush();
	}	
}
