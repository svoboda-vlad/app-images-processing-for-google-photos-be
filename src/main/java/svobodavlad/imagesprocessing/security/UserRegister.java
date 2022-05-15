package svobodavlad.imagesprocessing.security;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import svobodavlad.imagesprocessing.jpaentities.User;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegister {

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

	public User toUser() {
		User user = new User(username, givenName, familyName);
		user.setEmail(email);
		return user;
	}

}
