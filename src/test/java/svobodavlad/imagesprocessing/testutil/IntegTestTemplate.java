package svobodavlad.imagesprocessing.testutil;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ActiveProfiles("liquibase")
public class IntegTestTemplate extends MockMvcUtil {

}
