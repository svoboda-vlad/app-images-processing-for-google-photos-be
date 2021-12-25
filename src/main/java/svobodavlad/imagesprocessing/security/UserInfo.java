package svobodavlad.imagesprocessing.security;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import svobodavlad.imagesprocessing.jpaentities.User;
import svobodavlad.imagesprocessing.jpaentities.UserRoles;

@Data
@AllArgsConstructor
@NoArgsConstructor
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

	private Instant lastLoginDateTime;
	private Instant previousLoginDateTime;
	private Set<UserRoles> userRoles = new HashSet<UserRoles>();

	public User toUser(User user) {
		user.setFamilyName(familyName);
		user.setGivenName(givenName);
		return user;
	}

}