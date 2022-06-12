package svobodavlad.imagesprocessing;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
@ConfigurationProperties("admin")
public class AdminUserBean {

	private String username;

}