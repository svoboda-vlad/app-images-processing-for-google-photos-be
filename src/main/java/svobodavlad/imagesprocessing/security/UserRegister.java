package svobodavlad.imagesprocessing.security;

import java.util.ArrayList;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import svobodavlad.imagesprocessing.jpaentities.User;
import svobodavlad.imagesprocessing.jpaentities.User.LoginProvider;
import svobodavlad.imagesprocessing.jpaentities.UserRoles;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegister {

	@NotNull
	@Size(min = 1, max = 255)
	private String username;

	@NotNull
	@Size(min = 4, max = 100)
	private String password;

	@NotNull
	@Size(min = 1, max = 255)
	private String givenName;

	@NotNull
	@Size(min = 1, max = 255)
	private String familyName;

	public User toUserInternal(PasswordEncoder passwordEncoder) {
		User user = new User(username, passwordEncoder.encode(password), LoginProvider.INTERNAL, givenName, familyName, null, null, new ArrayList<UserRoles>());
		return user;
	}

	public User toUserGoogle(PasswordEncoder passwordEncoder) {
		User user = new User(username, passwordEncoder.encode(password), LoginProvider.GOOGLE, givenName, familyName, null, null, new ArrayList<UserRoles>());
		return user;
	}

}
