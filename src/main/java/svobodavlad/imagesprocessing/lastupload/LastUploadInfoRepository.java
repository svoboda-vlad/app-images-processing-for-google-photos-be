package svobodavlad.imagesprocessing.lastupload;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import svobodavlad.imagesprocessing.jpaentities.LastUploadInfo;
import svobodavlad.imagesprocessing.jpaentities.User;

public interface LastUploadInfoRepository extends JpaRepository<LastUploadInfo, Long> {
	
	Optional<LastUploadInfo> findByUser(User user);

	Optional<LastUploadInfo> findByUser_Username(String username);
	
}