package svobodavlad.imagesprocessing.security;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

	private LocalDateTime lastLoginDateTime;
	private LocalDateTime previousLoginDateTime;
	private List<UserRoles> userRoles = new ArrayList<UserRoles>();

	public User toUser(User user) {
		user.setFamilyName(familyName);
		user.setGivenName(givenName);
		return user;
	}

}