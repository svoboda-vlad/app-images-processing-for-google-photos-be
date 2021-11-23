package svobodavlad.imagesprocessing.security;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import svobodavlad.imagesprocessing.jpaentities.User;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByUsername(String username);

}