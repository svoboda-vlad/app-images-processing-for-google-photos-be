package svobodavlad.imagesprocessing.security;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import svobodavlad.imagesprocessing.jpaentities.User;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class UserTemplate {

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

	public User toUser(User user) {
		return user.setFamilyName(familyName).setGivenName(givenName).setEmail(email);
	}

}