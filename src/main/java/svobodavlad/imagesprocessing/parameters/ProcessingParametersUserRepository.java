package svobodavlad.imagesprocessing.parameters;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import svobodavlad.imagesprocessing.jpaentities.ProcessingParametersUser;
import svobodavlad.imagesprocessing.jpaentities.User;

public interface ProcessingParametersUserRepository extends JpaRepository<ProcessingParametersUser, Long> {
	
	Optional<ProcessingParametersUser> findByUser(User user);

}