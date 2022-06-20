package svobodavlad.imagesprocessing.security;

import java.time.Instant;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import svobodavlad.imagesprocessing.jpaentities.User;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class UserInfo {

	@NotNull
	@Size(min = 1, max = 255)
	private String username;

	@NotNull
	@Size(min = 1, max = 255)
	private String givenName;

	@NotNull
	@Size(min = 1, max = 255)
	private String familyName;
	
	@Size(min = 1, max = 255)
	private String email;

	private Instant lastLoginDateTime;
	private Instant previousLoginDateTime;

	public User toUser(User user) {
		user.setFamilyName(familyName);
		user.setGivenName(givenName);
		user.setEmail(email);
		return user;
	}

}