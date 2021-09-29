package svobodavlad.imagesprocessing.testutil;

import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.mockito.verification.VerificationMode;

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
}
